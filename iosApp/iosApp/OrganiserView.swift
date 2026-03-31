import SwiftUI
import Shared

struct OrganiserInfo {
    let name: String
    let email: String
    let phone: String
    let address: String
    let website: String
    let description: String
    let status: String
    let totalRevenue: Int
    let totalEvents: Int
    let activeEvents: Int
}

struct OrganiserView: View {
    @State private var org: OrganiserInfo? = nil

    private let brandBlue = Color(red: 0.31, green: 0.557, blue: 1.0)
    private let accentGold = Color(red: 1.0, green: 0.702, blue: 0.0)
    private let accentGreen = Color(red: 0.0, green: 0.784, blue: 0.325)
    private let cardBg = Color(red: 0.11, green: 0.145, blue: 0.216)
    private let surfaceBg = Color(red: 0.063, green: 0.098, blue: 0.161)
    private let textSecondary = Color(red: 0.541, green: 0.608, blue: 0.690)

    var body: some View {
        NavigationStack {
            Group {
                if let org = org {
                    ScrollView {
                        VStack(alignment: .leading, spacing: 0) {

                            // ── Header ──
                            VStack(alignment: .leading, spacing: 8) {
                                HStack(alignment: .center) {
                                    Text(org.name)
                                        .font(.system(size: 22, weight: .bold))
                                        .foregroundColor(.white)
                                    Spacer()
                                    Text(org.status.uppercased())
                                        .font(.system(size: 11, weight: .bold))
                                        .foregroundColor(accentGreen)
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 4)
                                        .background(accentGreen.opacity(0.12))
                                        .cornerRadius(20)
                                }
                                Text(org.description)
                                    .font(.system(size: 13))
                                    .foregroundColor(textSecondary)
                                    .lineSpacing(4)
                            }
                            .padding(20)
                            .background(surfaceBg)

                            Divider().background(Color(white: 0.12))

                            // ── Performance Stats ──
                            VStack(alignment: .leading, spacing: 12) {
                                Text("PERFORMANCE OVERVIEW")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(textSecondary)
                                    .kerning(1.0)

                                HStack(spacing: 10) {
                                    StatCard(value: "\(org.totalEvents)", label: "Total Events",
                                             valueColor: brandBlue, bg: cardBg)
                                    StatCard(value: "\(org.activeEvents)", label: "Active Events",
                                             valueColor: accentGreen, bg: cardBg)
                                    StatCard(
                                        value: org.totalRevenue > 0 ? "₹\(org.totalRevenue)" : "₹0",
                                        label: "Revenue",
                                        valueColor: accentGold,
                                        bg: cardBg
                                    )
                                }
                            }
                            .padding(16)

                            Divider().background(Color(white: 0.12))

                            // ── Contact Information ──
                            VStack(alignment: .leading, spacing: 12) {
                                Text("CONTACT INFORMATION")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(textSecondary)
                                    .kerning(1.0)

                                ContactRow(label: "Email", value: org.email)
                                ContactRow(label: "Phone", value: org.phone)
                                ContactRow(label: "Address", value: org.address)
                                HStack {
                                    Text("Website")
                                        .font(.system(size: 13))
                                        .foregroundColor(textSecondary)
                                        .frame(width: 72, alignment: .leading)
                                    Text(org.website)
                                        .font(.system(size: 14))
                                        .foregroundColor(brandBlue)
                                }
                            }
                            .padding(16)
                        }
                    }
                    .navigationTitle("Account")
                    .navigationBarTitleDisplayMode(.inline)
                } else {
                    ProgressView()
                        .tint(brandBlue)
                }
            }
            .onAppear(perform: loadOrganiser)
        }
    }

    private func loadOrganiser() {
        let o = OrganiserRepository().getOrganiser()
        org = OrganiserInfo(
            name: o.name,
            email: o.email,
            phone: o.phone,
            address: o.address,
            website: o.website,
            description: o.description_,
            status: o.status,
            totalRevenue: Int(o.totalRevenue),
            totalEvents: Int(o.totalEvents),
            activeEvents: Int(o.activeEvents)
        )
    }
}

private struct StatCard: View {
    let value: String
    let label: String
    let valueColor: Color
    let bg: Color

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(value)
                .font(.system(size: 26, weight: .bold))
                .foregroundColor(valueColor)
            Text(label)
                .font(.system(size: 11))
                .foregroundColor(Color(red: 0.541, green: 0.608, blue: 0.690))
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(bg)
        .cornerRadius(12)
    }
}

private struct ContactRow: View {
    let label: String
    let value: String

    var body: some View {
        HStack {
            Text(label)
                .font(.system(size: 13))
                .foregroundColor(Color(red: 0.541, green: 0.608, blue: 0.690))
                .frame(width: 72, alignment: .leading)
            Text(value)
                .font(.system(size: 14))
                .foregroundColor(Color(white: 0.87))
        }
    }
}
