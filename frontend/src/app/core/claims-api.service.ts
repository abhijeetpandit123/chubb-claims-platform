import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Claim, ClaimType, Dashboard, Market } from "./models";
@Injectable({ providedIn: "root" })
export class ClaimsApiService {
  constructor(private http: HttpClient) {}
  list() {
    return this.http.get<Claim[]>("/api/claims");
  }
  create(payload: {
    type: ClaimType;
    market: Market;
    description: string;
    estimatedAmount: number;
  }) {
    return this.http.post<Claim>("/api/claims", payload);
  }
  dashboard() {
    return this.http.get<Dashboard>("/api/dashboard");
  }
  assign(id: string) {
    return this.http.post<Claim>(`/api/claims/${id}/assign`, {});
  }
  requestInfo(id: string, text: string) {
    return this.http.post<Claim>(`/api/claims/${id}/request-info`, { text });
  }
  supplyInfo(id: string, text: string) {
    return this.http.post<Claim>(`/api/claims/${id}/provide-info`, { text });
  }
  decide(id: string, approved: boolean, reason: string) {
    return this.http.post<Claim>(`/api/claims/${id}/decision`, {
      approved,
      reason,
    });
  }
  settle(id: string) {
    return this.http.post<Claim>(`/api/claims/${id}/settle`, {});
  }
}
