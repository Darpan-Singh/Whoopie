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

// Poster color schemes — cycle by index
private let posterSchemes: [(top: Color, bottom: Color)] = [
    (Color(red: 0.102, green: 0.227, blue: 0.561), Color(red: 0.027, green: 0.055, blue: 0.102)), // blue
    (Color(red: 0.420, green: 0.106, blue: 0.420), Color(red: 0.027, green: 0.012, blue: 0.039)), // purple
    (Color(red: 0.000, green: 0.349, blue: 0.302), Color(red: 0.012, green: 0.039, blue: 0.031)), // teal
]

struct EventListView: View {
    @State private var events: [EventItem] = []

    var body: some View {
        NavigationStack {
            List(Array(events.enumerated()), id: \.element.id) { index, event in
                NavigationLink(destination: EventDetailView(event: event)) {
                    EventCard(event: event, schemeIndex: index)
                }
                .listRowInsets(EdgeInsets(top: 6, leading: 16, bottom: 6, trailing: 16))
                .listRowBackground(Color.clear)
                .listRowSeparator(.hidden)
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

private struct EventCard: View {
    let event: EventItem
    let schemeIndex: Int

    private var isActive: Bool { event.status.lowercased() == "active" }
    private var scheme: (top: Color, bottom: Color) {
        posterSchemes[schemeIndex % posterSchemes.count]
    }

    var body: some View {
        VStack(spacing: 0) {

            // ── Poster area ──
            ZStack(alignment: .bottomLeading) {
                LinearGradient(
                    colors: [scheme.top, scheme.bottom],
                    startPoint: .top,
                    endPoint: .bottom
                )
                .frame(height: 150)

                VStack(alignment: .leading, spacing: 3) {
                    Text(event.eventName)
                        .font(.system(size: 17, weight: .bold))
                        .foregroundColor(.white)
                        .shadow(color: .black.opacity(0.5), radius: 3, y: 1)
                    Text(event.location)
                        .font(.system(size: 12))
                        .foregroundColor(.white.opacity(0.75))
                }
                .padding(14)
            }

            // ── Info row ──
            HStack {
                Text(event.date)
                    .font(.system(size: 13))
                    .foregroundColor(Color(red: 0.541, green: 0.608, blue: 0.690))
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
            .padding(.horizontal, 14)
            .padding(.vertical, 12)
            .background(Color(red: 0.11, green: 0.145, blue: 0.216))
        }
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}
