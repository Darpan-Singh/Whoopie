import SwiftUI

// Mirrors OrganiserRepository.kt in the shared module
private let organiserData = OrganiserInfo(
    name: "Updated Tech Events Co",
    email: "contact@techevents.co",
    phone: "+1-555-0123",
    address: "123 Tech Street, Silicon Valley, CA",
    website: "https://techevents.co",
    description: "Leading technology and innovation event organizer",
    status: "active",
    totalRevenue: 0,
    totalEvents: 0,
    activeEvents: 0
)

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
    let org = organiserData

    var body: some View {
        NavigationStack {
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
        }
    }
}
