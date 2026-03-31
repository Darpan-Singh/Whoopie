import SwiftUI

struct LoginView: View {
    @Binding var isLoggedIn: Bool
    @State private var username = ""
    @State private var password = ""
    @State private var showError = false

    var body: some View {
        VStack(spacing: 24) {
            Spacer()

            Text("Whoopie")
                .font(.largeTitle.bold())
            Text("Thrillathon Client")
                .foregroundColor(.secondary)

            Spacer()

            VStack(spacing: 16) {
                TextField("Username", text: $username)
                    .textFieldStyle(.roundedBorder)
                    .autocorrectionDisabled()
                    .textInputAutocapitalization(.never)

                SecureField("Password", text: $password)
                    .textFieldStyle(.roundedBorder)

                if showError {
                    Text("Invalid username or password")
                        .foregroundColor(.red)
                        .font(.caption)
                }

                Button(action: login) {
                    Text("Login")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.borderedProminent)
            }
            .padding(.horizontal, 32)

            Spacer()
        }
    }

    private func login() {
        if username == "admin" && password == "admin123" {
            showError = false
            isLoggedIn = true
        } else {
            showError = true
        }
    }
}
