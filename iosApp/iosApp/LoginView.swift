import SwiftUI

struct LoginView: View {
    @Binding var isLoggedIn: Bool
    @State private var username = ""
    @State private var password = ""
    @State private var showError = false

    var body: some View {
        ZStack {
            Color(red: 0.031, green: 0.055, blue: 0.102).ignoresSafeArea()

            ScrollView {
                VStack(spacing: 0) {
                    Spacer().frame(height: 80)

                    // Brand mark
                    ZStack {
                        RoundedRectangle(cornerRadius: 16)
                            .fill(Color(red: 0.31, green: 0.557, blue: 1.0))
                            .frame(width: 64, height: 64)
                        Text("W")
                            .font(.system(size: 28, weight: .bold))
                            .foregroundColor(.white)
                    }
                    .padding(.bottom, 20)

                    // App name
                    Text("Whoopie")
                        .font(.system(size: 34, weight: .bold))
                        .foregroundColor(.white)
                        .padding(.bottom, 6)

                    // Tagline
                    Text("Smart Facial Ticketing for Events")
                        .font(.system(size: 14))
                        .foregroundColor(Color(white: 0.54))
                        .padding(.bottom, 8)

                    // Portal label
                    Text("ORGANISER PORTAL")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(Color(red: 0.31, green: 0.557, blue: 1.0))
                        .kerning(1.4)
                        .padding(.bottom, 40)

                    // Form card
                    VStack(spacing: 16) {
                        TextField("Username", text: $username)
                            .textFieldStyle(.plain)
                            .padding(14)
                            .background(Color(red: 0.063, green: 0.098, blue: 0.161))
                            .cornerRadius(10)
                            .foregroundColor(.white)
                            .autocorrectionDisabled()
                            .textInputAutocapitalization(.never)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color(white: 0.12), lineWidth: 1)
                            )

                        SecureField("Password", text: $password)
                            .textFieldStyle(.plain)
                            .padding(14)
                            .background(Color(red: 0.063, green: 0.098, blue: 0.161))
                            .cornerRadius(10)
                            .foregroundColor(.white)
                            .overlay(
                                RoundedRectangle(cornerRadius: 10)
                                    .stroke(Color(white: 0.12), lineWidth: 1)
                            )

                        if showError {
                            HStack {
                                Image(systemName: "exclamationmark.circle.fill")
                                    .foregroundColor(Color(red: 1, green: 0.322, blue: 0.322))
                                Text("Invalid credentials. Please try again.")
                                    .font(.caption)
                                    .foregroundColor(Color(red: 1, green: 0.322, blue: 0.322))
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                        }

                        Button(action: login) {
                            Text("Sign In")
                                .font(.system(size: 16, weight: .semibold))
                                .frame(maxWidth: .infinity)
                                .frame(height: 52)
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(Color(red: 0.31, green: 0.557, blue: 1.0))
                    }
                    .padding(24)
                    .background(Color(red: 0.11, green: 0.145, blue: 0.216))
                    .cornerRadius(16)
                    .padding(.horizontal, 24)

                    Spacer().frame(height: 48)

                    Text("Powered by Whoopie · Facial Ticketing Platform")
                        .font(.system(size: 11))
                        .foregroundColor(Color(white: 0.29))

                    Spacer().frame(height: 40)
                }
            }
        }
        .preferredColorScheme(.dark)
    }

    private func login() {
        if username == "admin" && password == "admin123" {
            showError = false
            isLoggedIn = true
        } else {
            showError = true
        }
    }
}
