import SwiftUI

struct EventDetailView: View {
    let event: EventItem

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                Text(event.eventName)
                    .font(.title.bold())

                VStack(alignment: .leading, spacing: 10) {
                    Label(event.date, systemImage: "calendar")
                    Label(event.location, systemImage: "mappin.circle")
                }
                .foregroundColor(.secondary)

                HStack {
                    Text("Status:")
                        .bold()
                    Text(event.status)
                        .foregroundColor(event.status == "Active" ? .green : .orange)
                }

                Divider()

                VStack(alignment: .leading, spacing: 8) {
                    Text("Description")
                        .font(.headline)
                    Text(event.description)
                        .foregroundColor(.secondary)
                }
            }
            .padding()
        }
        .navigationTitle("Event Details")
        .navigationBarTitleDisplayMode(.inline)
    }
}
