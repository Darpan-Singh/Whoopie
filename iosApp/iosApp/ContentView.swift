import SwiftUI

struct ContentView: View {
    var body: some View {
        TabView {
            EventListView()
                .tabItem {
                    Label("Events", systemImage: "list.bullet.rectangle")
                }

            DashboardView()
                .tabItem {
                    Label("Face Scan", systemImage: "faceid")
                }

            OrganiserView()
                .tabItem {
                    Label("Organiser", systemImage: "person.crop.circle")
                }
        }
    }
}
