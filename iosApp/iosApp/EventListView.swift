import SwiftUI

struct EventItem: Identifiable {
    let id: String
    let eventName: String
    let date: String
    let status: String
    let description: String
    let location: String
}

// Mirrors EventRepository.kt in the shared module
private let sampleEvents: [EventItem] = [
    EventItem(id: "1", eventName: "Thrillathon 2026", date: "27 March 2026",
              status: "Active", description: "India's biggest tech + event fest", location: "Mumbai"),
    EventItem(id: "2", eventName: "Music Night", date: "5 April 2026",
              status: "Active", description: "Live DJ + Concert", location: "Bangalore"),
    EventItem(id: "3", eventName: "Startup Expo", date: "12 April 2026",
              status: "Upcoming", description: "Startup showcase event", location: "Delhi")
]

struct EventListView: View {
    var body: some View {
        NavigationStack {
            List(sampleEvents) { event in
                NavigationLink(destination: EventDetailView(event: event)) {
                    EventRow(event: event)
                }
            }
            .navigationTitle("Events")
        }
    }
}

private struct EventRow: View {
    let event: EventItem

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(event.eventName)
                .font(.headline)
            Text(event.date)
                .font(.subheadline)
                .foregroundColor(.secondary)
            Text(event.status)
                .font(.caption.bold())
                .foregroundColor(event.status == "Active" ? .green : .orange)
                .padding(.horizontal, 8)
                .padding(.vertical, 2)
                .background(
                    Capsule().fill(event.status == "Active"
                        ? Color.green.opacity(0.12)
                        : Color.orange.opacity(0.12))
                )
        }
        .padding(.vertical, 4)
    }
}
