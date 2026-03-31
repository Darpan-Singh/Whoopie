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
                .listRowBackground(Color(red: 0.11, green: 0.145, blue: 0.216))
                .listRowSeparatorTint(Color(white: 0.12))
            }
            .scrollContentBackground(.hidden)
            .background(Color(red: 0.031, green: 0.055, blue: 0.102))
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

    private var isActive: Bool { event.status.lowercased() == "active" }

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(event.eventName)
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundColor(.white)
                Spacer()
                Text(event.status.uppercased())
                    .font(.system(size: 10, weight: .bold))
                    .foregroundColor(isActive
                        ? Color(red: 0.0, green: 0.784, blue: 0.325)
                        : Color(red: 1.0, green: 0.596, blue: 0.0))
                    .padding(.horizontal, 9)
                    .padding(.vertical, 3)
                    .background(
                        Capsule().fill(isActive
                            ? Color(red: 0.0, green: 0.784, blue: 0.325).opacity(0.12)
                            : Color(red: 1.0, green: 0.596, blue: 0.0).opacity(0.12))
                    )
            }
            Text(event.date)
                .font(.system(size: 13))
                .foregroundColor(Color(red: 0.541, green: 0.608, blue: 0.690))
            if !event.location.isEmpty {
                Text(event.location)
                    .font(.system(size: 13))
                    .foregroundColor(Color(red: 0.541, green: 0.608, blue: 0.690))
            }
        }
        .padding(.vertical, 4)
    }
}
