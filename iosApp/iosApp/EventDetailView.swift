import SwiftUI

struct EventDetailView: View {
    let event: EventItem

    private var isActive: Bool { event.status.lowercased() == "active" }
    private let brandBlue = Color(red: 0.31, green: 0.557, blue: 1.0)
    private let accentGreen = Color(red: 0.0, green: 0.784, blue: 0.325)
    private let accentOrange = Color(red: 1.0, green: 0.596, blue: 0.0)
    private let bgPrimary = Color(red: 0.031, green: 0.055, blue: 0.102)
    private let textSecondary = Color(red: 0.541, green: 0.608, blue: 0.690)
    private let textMuted = Color(red: 0.29, green: 0.333, blue: 0.376)

    var body: some View {
        ZStack(alignment: .bottom) {
            bgPrimary.ignoresSafeArea()

            ScrollView {
                VStack(alignment: .leading, spacing: 0) {

                    // ── Event name + status ──
                    VStack(alignment: .leading, spacing: 10) {
                        HStack(alignment: .top) {
                            Text(event.eventName)
                                .font(.system(size: 22, weight: .bold))
                                .foregroundColor(.white)
                                .frame(maxWidth: .infinity, alignment: .leading)
                            Text(event.status.uppercased())
                                .font(.system(size: 11, weight: .bold))
                                .foregroundColor(isActive ? accentGreen : accentOrange)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 5)
                                .background(
                                    Capsule().fill(isActive
                                        ? accentGreen.opacity(0.12)
                                        : accentOrange.opacity(0.12))
                                )
                        }
                    }
                    .padding(20)
                    .background(Color(red: 0.063, green: 0.098, blue: 0.161))

                    Divider().background(Color(white: 0.12))

                    // ── Details ──
                    VStack(alignment: .leading, spacing: 14) {
                        DetailRow(label: "Date", value: event.date, textSecondary: textSecondary, textMuted: textMuted)
                        if !event.location.isEmpty {
                            DetailRow(label: "Location", value: event.location, textSecondary: textSecondary, textMuted: textMuted)
                        }
                    }
                    .padding(20)

                    Divider().background(Color(white: 0.12))

                    // ── Description ──
                    VStack(alignment: .leading, spacing: 8) {
                        Text("DESCRIPTION")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(textMuted)
                            .kerning(1.0)

                        Text(event.description)
                            .font(.system(size: 14))
                            .foregroundColor(textSecondary)
                            .lineSpacing(5)
                    }
                    .padding(20)

                    // Bottom padding so content doesn't hide behind the button
                    Spacer().frame(height: 100)
                }
            }

            // ── Pinned CTA ──
            VStack(spacing: 0) {
                Divider().background(Color(white: 0.12))
                Button {
                    // Navigate to face check-in
                } label: {
                    Label("Begin Face Check-In", systemImage: "faceid")
                        .font(.system(size: 16, weight: .semibold))
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                }
                .buttonStyle(.borderedProminent)
                .tint(brandBlue)
                .padding(16)
            }
            .background(Color(red: 0.063, green: 0.098, blue: 0.161))
        }
        .navigationTitle("Event Details")
        .navigationBarTitleDisplayMode(.inline)
    }
}

private struct DetailRow: View {
    let label: String
    let value: String
    let textSecondary: Color
    let textMuted: Color

    var body: some View {
        HStack(alignment: .top) {
            Text(label.uppercased())
                .font(.system(size: 11, weight: .medium))
                .foregroundColor(textMuted)
                .kerning(0.8)
                .frame(width: 72, alignment: .leading)
            Text(value)
                .font(.system(size: 14))
                .foregroundColor(textSecondary)
        }
    }
}
