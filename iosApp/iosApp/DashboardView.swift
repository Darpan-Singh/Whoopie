import SwiftUI
import AVFoundation
import Vision

// iOS equivalent of DashboardFragment — uses AVFoundation + Vision instead of CameraX + ML Kit
struct DashboardView: View {
    @StateObject private var detector = FaceDetector()

    var body: some View {
        NavigationStack {
            ZStack {
                if let error = detector.cameraError {
                    VStack(spacing: 16) {
                        Image(systemName: "camera.slash")
                            .font(.system(size: 52))
                            .foregroundColor(.secondary)
                        Text(error)
                            .multilineTextAlignment(.center)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 32)
                        Button("Open Settings") {
                            if let url = URL(string: UIApplication.openSettingsURLString) {
                                UIApplication.shared.open(url)
                            }
                        }
                        .buttonStyle(.borderedProminent)
                    }
                } else {
                    CameraPreview(session: detector.session)
                        .ignoresSafeArea()

                    VStack {
                        // Top label bar
                        HStack {
                            Image(systemName: "faceid")
                                .foregroundColor(Color(red: 0.31, green: 0.557, blue: 1.0))
                            Text("Face Check-In")
                                .font(.system(size: 15, weight: .semibold))
                                .foregroundColor(.white)
                        }
                        .padding(.horizontal, 20)
                        .padding(.vertical, 10)
                        .background(.black.opacity(0.6))
                        .clipShape(Capsule())
                        .padding(.top, 20)

                        Spacer()

                        VStack(spacing: 12) {
                            if detector.faceDetected {
                                ProgressView(value: detector.progress)
                                    .progressViewStyle(.linear)
                                    .tint(Color(red: 0.31, green: 0.557, blue: 1.0))
                                    .frame(width: 240)

                                Text(detector.progress >= 1.0
                                     ? "Check-In Complete"
                                     : "Hold still — scanning…")
                                    .font(.system(size: 15, weight: .medium))
                                    .foregroundColor(.white)
                            } else {
                                Text("Position your face within the frame")
                                    .font(.system(size: 15, weight: .medium))
                                    .foregroundColor(.white)
                                    .multilineTextAlignment(.center)
                            }
                        }
                        .padding(.horizontal, 24)
                        .padding(.vertical, 16)
                        .background(.black.opacity(0.6))
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .padding(.bottom, 52)
                    }
                }
            }
            .navigationTitle("Check-In")
            .onAppear { detector.start() }
            .onDisappear { detector.stop() }
        }
    }
}

// MARK: - Face detection logic

final class FaceDetector: NSObject, ObservableObject, AVCaptureVideoDataOutputSampleBufferDelegate {
    let session = AVCaptureSession()

    @Published var faceDetected = false
    @Published var progress: Double = 0.0
    @Published var cameraError: String? = nil

    private var progressTimer: Timer?
    private var captured = false

    func start() {
        guard !session.isRunning else { return }

        let status = AVCaptureDevice.authorizationStatus(for: .video)
        if status == .denied || status == .restricted {
            DispatchQueue.main.async {
                self.cameraError = "Camera access denied. Enable it in Settings."
            }
            return
        }

        if status == .notDetermined {
            AVCaptureDevice.requestAccess(for: .video) { [weak self] granted in
                if granted { self?.configureSession() }
                else {
                    DispatchQueue.main.async {
                        self?.cameraError = "Camera access denied. Enable it in Settings."
                    }
                }
            }
            return
        }

        configureSession()
    }

    private func configureSession() {
        session.sessionPreset = .high

        guard let device = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .front) else {
            DispatchQueue.main.async { self.cameraError = "No front camera found on this device." }
            return
        }

        do {
            let input = try AVCaptureDeviceInput(device: device)
            let output = AVCaptureVideoDataOutput()
            output.setSampleBufferDelegate(self, queue: DispatchQueue(label: "whoopie.face.queue"))

            session.beginConfiguration()
            if session.canAddInput(input)   { session.addInput(input) }
            if session.canAddOutput(output) { session.addOutput(output) }
            session.commitConfiguration()

            DispatchQueue.global(qos: .userInitiated).async { self.session.startRunning() }
        } catch {
            DispatchQueue.main.async { self.cameraError = "Camera setup failed: \(error.localizedDescription)" }
        }
    }

    func stop() {
        session.stopRunning()
        progressTimer?.invalidate()
    }

    func captureOutput(_ output: AVCaptureOutput,
                       didOutput sampleBuffer: CMSampleBuffer,
                       from connection: AVCaptureConnection) {
        guard let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else { return }

        let request = VNDetectFaceRectanglesRequest { [weak self] req, _ in
            guard let self else { return }
            let found = !(req.results?.isEmpty ?? true)
            DispatchQueue.main.async {
                self.faceDetected = found
                if found && !self.captured {
                    self.startProgress()
                } else if !found {
                    self.progressTimer?.invalidate()
                }
            }
        }
        try? VNImageRequestHandler(cvPixelBuffer: pixelBuffer, options: [:]).perform([request])
    }

    private func startProgress() {
        guard progressTimer?.isValid != true else { return }
        progressTimer = Timer.scheduledTimer(withTimeInterval: 0.05, repeats: true) { [weak self] t in
            guard let self else { return }
            if self.progress < 1.0 {
                self.progress = min(self.progress + 0.02, 1.0)
            } else {
                t.invalidate()
                self.captured = true
                // reset after 2 s so next check-in can happen
                DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                    self.progress = 0
                    self.captured = false
                }
            }
        }
    }
}

// MARK: - Camera preview bridge

struct CameraPreview: UIViewRepresentable {
    let session: AVCaptureSession

    func makeUIView(context: Context) -> UIView {
        let view = UIView(frame: UIScreen.main.bounds)
        let layer = AVCaptureVideoPreviewLayer(session: session)
        layer.videoGravity = .resizeAspectFill
        layer.frame = view.bounds
        view.layer.addSublayer(layer)
        return view
    }

    func updateUIView(_ uiView: UIView, context: Context) {}
}
