import { Component, OnInit, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { HttpErrorResponse } from "@angular/common/http";
import { AuthService } from "./core/auth.service";
import { ClaimsApiService } from "./core/claims-api.service";
import { Claim, ClaimType, Dashboard, Market } from "./core/models";
@Component({
  selector: "app-root",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./app.component.html",
})
export class AppComponent implements OnInit {
  auth = inject(AuthService);
  api = inject(ClaimsApiService);
  username = "claimant1";
  password = "demo123";
  claims: Claim[] = [];
  dashboard: Dashboard | null = null;
  type: ClaimType = "MOTOR";
  market: Market = "SG";
  description = "";
  amount: number | null = null;
  note = "";
  error = "";
  busy = false;
  ngOnInit() {
    this.auth.restore(() => this.refresh());
  }
  login() {
    this.error = "";
    this.auth
      .signIn(this.username, this.password)
      .subscribe({
        next: () => this.refresh(),
        error: () => (this.error = "Invalid demo credentials"),
      });
  }
  logout() {
    this.auth.signOut();
    this.claims = [];
    this.dashboard = null;
    this.error = "";
  }
  refresh() {
    if (!this.auth.user()) return;
    this.api
      .list()
      .subscribe({
        next: (c) => (this.claims = c),
        error: (e) => this.failure(e),
      });
    if (this.staff)
      this.api
        .dashboard()
        .subscribe({
          next: (d) => (this.dashboard = d),
          error: (e) => this.failure(e),
        });
  }
  get staff() {
    return this.auth.user()?.role === "STAFF";
  }
  submit() {
    if (!this.description.trim() || !this.amount || this.amount <= 0) {
      this.error = "Enter a description and positive amount.";
      return;
    }
    this.run(
      () =>
        this.api.create({
          type: this.type,
          market: this.market,
          description: this.description,
          estimatedAmount: this.amount!,
        }),
      () => {
        this.description = "";
        this.amount = null;
      },
    );
  }
  assign(c: Claim) {
    this.run(() => this.api.assign(c.id));
  }
  request(c: Claim) {
    const text = window.prompt("Information required from claimant:");
    if (text?.trim()) this.run(() => this.api.requestInfo(c.id, text));
  }
  provide(c: Claim) {
    const text = window.prompt("Additional information:");
    if (text?.trim()) this.run(() => this.api.supplyInfo(c.id, text));
  }
  decision(c: Claim, approved: boolean) {
    const reason = window.prompt(
      approved ? "Approval reason:" : "Rejection reason:",
    );
    if (reason?.trim()) this.run(() => this.api.decide(c.id, approved, reason));
  }
  settle(c: Claim) {
    this.run(() => this.api.settle(c.id));
  }
  private run(
    action: () => import("rxjs").Observable<Claim>,
    done?: () => void,
  ) {
    this.error = "";
    this.busy = true;
    action().subscribe({
      next: () => {
        this.busy = false;
        done?.();
        this.refresh();
      },
      error: (e) => {
        this.busy = false;
        this.failure(e);
      },
    });
  }
  private failure(e: HttpErrorResponse) {
    this.error =
      e.error?.error ||
      `Request failed (${e.status}). Check backend and permissions.`;
  }
}
