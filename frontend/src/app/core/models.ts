export type ClaimStatus =
  | "SUBMITTED"
  | "IN_REVIEW"
  | "INFO_REQUESTED"
  | "APPROVED"
  | "REJECTED"
  | "SETTLED";
export type ClaimType = "MOTOR" | "PROPERTY";
export type Market = "AU" | "SG" | "HK" | "MY" | "TH" | "VN";
export interface Claim {
  id: string;
  version: number;
  claimantUsername: string;
  type: ClaimType;
market: Market;
  description: string;
  estimatedAmount: number;
  currency: string;
  status: ClaimStatus;
  assignedOfficer: string | null;
  informationRequest: string | null;
  additionalInformation: string | null;
  decisionReason: string | null;
  createdAt: string;
  updatedAt: string;
}
export interface Exposure {
  market: string;
  currency: string;
  amount: number;
  outstandingCount: number;
}
export interface Dashboard {
  submitted: number;
  inReview: number;
  awaitingInfo: number;
  approved: number;
  rejected: number;
  settled: number;
  exposure: Exposure[];
}
export interface CurrentUser {
  username: string;
  role: "STAFF" | "CLAIMANT";
}
