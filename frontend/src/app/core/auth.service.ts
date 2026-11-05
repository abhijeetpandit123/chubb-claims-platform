import { Injectable, signal } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { tap } from "rxjs";
import { CurrentUser } from "./models";

@Injectable({ providedIn: "root" })

export class AuthService {
  readonly user = signal<CurrentUser | null>(null);
  constructor(private http: HttpClient) {}
  signIn(username: string, password: string) {
    sessionStorage.setItem("demoBasicAuth", btoa(`${username}:${password}`));
    return this.http
      .get<CurrentUser>("/api/me")
      .pipe(
        tap({ next: (u) => this.user.set(u), error: () => this.signOut() }),
      );
  }
  signOut() {
    sessionStorage.removeItem("demoBasicAuth");
    this.user.set(null);
  }
  restore(onReady: () => void) {
    if (sessionStorage.getItem("demoBasicAuth"))
      this.http.get<CurrentUser>("/api/me").subscribe({
        next: (u) => {
          this.user.set(u);
          onReady();
        },
        error: () => this.signOut(),
      });
  }
}
