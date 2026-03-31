import SwiftUI
import Shared

struct EventItem: Identifiable {
    let id: String
    let eventName: String
    let date: String
    let status: String
    let description: String
    let location: String
}

struct EventListView: View {
    @State private var events: [EventItem] = []

    var body: some View {
        NavigationStack {
            List(events) { event in
                NavigationLink(destination: EventDetailView(event: event)) {
                    EventRow(event: event)
                }
            }
            .navigationTitle("Events")
            .onAppear(perform: loadEvents)
        }
    }

    private func loadEvents() {
        events = EventRepository().getEvents().map { e in
            EventItem(
                id: e.id,
                eventName: e.eventName,
                date: e.date,
                status: e.status,
                description: e.description_,
                location: e.location
            )
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
