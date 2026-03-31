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

    var body: some View {
        NavigationStack {
            Group {
                if let org = org {
                    List {
                        Section("Contact") {
                            LabeledContent("Email", value: org.email)
                            LabeledContent("Phone", value: org.phone)
                            LabeledContent("Website", value: org.website)
                            LabeledContent("Address", value: org.address)
                        }
                        Section("About") {
                            Text(org.description)
                                .foregroundColor(.secondary)
                        }
                        Section("Stats") {
                            LabeledContent("Total Events", value: "\(org.totalEvents)")
                            LabeledContent("Active Events", value: "\(org.activeEvents)")
                            LabeledContent("Revenue", value: "₹\(org.totalRevenue)")
                            LabeledContent("Status", value: org.status.uppercased())
                        }
                    }
                    .navigationTitle(org.name)
                } else {
                    ProgressView()
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
