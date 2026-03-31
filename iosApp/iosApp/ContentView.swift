import SwiftUI

struct ContentView: View {
    @Binding var isLoggedIn: Bool

    var body: some View {
        TabView {
            EventListView()
                .tabItem {
                    Label("Events", systemImage: "ticket")
                }

            DashboardView()
                .tabItem {
                    Label("Check-In", systemImage: "faceid")
                }

            OrganiserView()
                .tabItem {
                    Label("Account", systemImage: "building.2")
                }

            ProfileView(isLoggedIn: $isLoggedIn)
                .tabItem {
                    Label("Settings", systemImage: "gearshape")
                }
        }
        .tint(Color(red: 0.31, green: 0.557, blue: 1.0))
        .preferredColorScheme(.dark)
    }
}

struct ProfileView: View {
    @Binding var isLoggedIn: Bool

    var body: some View {
        NavigationStack {
            List {
                Section("Whoopie") {
                    LabeledContent("Platform", value: "Facial Ticketing")
                    LabeledContent("Version", value: "1.0")
                }
                Section {
                    Button(role: .destructive) {
                        isLoggedIn = false
                    } label: {
                        Label("Sign Out", systemImage: "rectangle.portrait.and.arrow.right")
                    }
                }
            }
            .navigationTitle("Settings")
        }
    }
}
