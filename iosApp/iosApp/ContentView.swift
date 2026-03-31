import SwiftUI

struct ContentView: View {
    @Binding var isLoggedIn: Bool

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

            ProfileView(isLoggedIn: $isLoggedIn)
                .tabItem {
                    Label("Profile", systemImage: "person.circle")
                }
        }
    }
}

struct ProfileView: View {
    @Binding var isLoggedIn: Bool

    var body: some View {
        NavigationStack {
            List {
                Section {
                    Button(role: .destructive) {
                        isLoggedIn = false
                    } label: {
                        Label("Log Out", systemImage: "rectangle.portrait.and.arrow.right")
                    }
                }
            }
            .navigationTitle("Profile")
        }
    }
}
