package com.chubb.claims.service;
import com.chubb.claims.api.ClaimsDto.*;
import com.chubb.claims.domain.*;
import com.chubb.claims.infrastructure.ClaimRepository;
import java.math.BigDecimal;import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ClaimsService {
 private final ClaimRepository repository;private final ClaimEventPublisher events;
 public ClaimsService(ClaimRepository repository, ClaimEventPublisher events){this.repository=repository;this.events=events;}
 @Transactional public ClaimView create(String username,CreateClaim command){
  Claim c=repository.save(new Claim(username,command.type(),command.market(),command.description(),command.estimatedAmount()));
  events.claimChanged(c.getId(),"CLAIM_SUBMITTED",c.getStatus().name());return ClaimView.from(c);
 }
 @Transactional(readOnly=true) public List<ClaimView> mine(String username){return repository.findByClaimantUsernameOrderByCreatedAtDesc(username).stream().map(ClaimView::from).toList();}
 @Transactional(readOnly=true) public List<ClaimView> all(){return repository.findAllByOrderByCreatedAtDesc().stream().map(ClaimView::from).toList();}
 @Transactional(readOnly=true) public ClaimView one(UUID id,String username,boolean staff){Claim c=get(id);if(!staff&&!c.getClaimantUsername().equals(username))throw new SecurityException("Claim not visible");return ClaimView.from(c);}
 @Transactional public ClaimView assign(UUID id,String officer){Claim c=get(id);c.assignTo(officer);return persist(c,"CLAIM_ASSIGNED");}
 @Transactional public ClaimView requestInfo(UUID id,String officer,TextCommand input){Claim c=get(id);c.requestInformation(officer,input.text());return persist(c,"INFORMATION_REQUESTED");}
 @Transactional public ClaimView supplyInfo(UUID id,String claimant,TextCommand input){Claim c=get(id);c.supplyInformation(claimant,input.text());return persist(c,"INFORMATION_SUPPLIED");}
 @Transactional public ClaimView decide(UUID id,String officer,DecisionCommand input){Claim c=get(id);c.decide(officer,input.approved(),input.reason());return persist(c,"CLAIM_DECIDED");}
 @Transactional public ClaimView settle(UUID id,String officer){Claim c=get(id);c.settle(officer);return persist(c,"CLAIM_SETTLED");}
 @Transactional(readOnly=true) public Dashboard dashboard(){List<Claim> claims=repository.findAll();
  Map<ClaimStatus,Long> byStatus=claims.stream().collect(Collectors.groupingBy(Claim::getStatus,Collectors.counting()));
  // Never aggregate liabilities in incompatible currencies.
  Map<String,List<Claim>> outstanding=claims.stream().filter(c->c.getStatus()!=ClaimStatus.REJECTED&&c.getStatus()!=ClaimStatus.SETTLED)
   .collect(Collectors.groupingBy(c->c.getMarket().name()+":"+c.getCurrency()));
  List<Exposure> exposures=outstanding.entrySet().stream().map(e->{String[] parts=e.getKey().split(":");
   BigDecimal sum=e.getValue().stream().map(Claim::getEstimatedAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
   return new Exposure(parts[0],parts[1],sum,e.getValue().size());}).sorted(Comparator.comparing(Exposure::market)).toList();
  return new Dashboard(count(byStatus,ClaimStatus.SUBMITTED),count(byStatus,ClaimStatus.IN_REVIEW),count(byStatus,ClaimStatus.INFO_REQUESTED),count(byStatus,ClaimStatus.APPROVED),count(byStatus,ClaimStatus.REJECTED),count(byStatus,ClaimStatus.SETTLED),exposures);
 }
 private long count(Map<ClaimStatus,Long> m,ClaimStatus s){return m.getOrDefault(s,0L);}
 private Claim get(UUID id){return repository.findById(id).orElseThrow(()->new NoSuchElementException("Claim not found"));}
 private ClaimView persist(Claim c,String event){repository.saveAndFlush(c);events.claimChanged(c.getId(),event,c.getStatus().name());return ClaimView.from(c);}
}
