package com.membership.demo;

import com.membership.service.MembershipTransitionService;
import com.membership.transition.TransitionResult;

public class TransitionDemo {
    public static void main(String[] args) {
        MembershipTransitionService service = new MembershipTransitionService();
        
        service.displayRulesTable();
        
        System.out.println("=== TESTING TRANSITIONS ===\n");
        
        testTransition(service, "SILVER", "GOLD", false, "Upgrade");
        testTransition(service, "GOLD", "SILVER", false, "Downgrade (mid-subscription)");
        testTransition(service, "GOLD", "SILVER", true, "Downgrade (at subscription end)");
        testTransition(service, "PLATINUM", "SILVER", true, "Direct downgrade PLATINUM to SILVER");
        testTransition(service, "PLATINUM", "GOLD", true, "Downgrade PLATINUM to GOLD");
        
        System.out.println("\n=== TESTING CANCELLATIONS ===\n");
        testCancellation(service, "SILVER", false);
        testCancellation(service, "GOLD", false);
        testCancellation(service, "GOLD", true);
        testCancellation(service, "PLATINUM", true);
    }
    
    private static void testTransition(MembershipTransitionService service, 
                                      String from, String to, 
                                      boolean isEnd, String description) {
        TransitionResult result = service.requestDowngrade(from, to, isEnd);
        System.out.println(description + ": " + from + " -> " + to);
        System.out.println("  Status: " + (result.isAllowed() ? "✓ ALLOWED" : "✗ DENIED"));
        System.out.println("  Reason: " + result.getMessage());
        System.out.println();
    }
    
    private static void testCancellation(MembershipTransitionService service, 
                                        String tier, boolean isEnd) {
        TransitionResult result = service.requestCancellation(tier, isEnd);
        System.out.println("Cancel " + tier + (isEnd ? " (at end)" : " (mid-subscription)"));
        System.out.println("  Status: " + (result.isAllowed() ? "✓ ALLOWED" : "✗ DENIED"));
        System.out.println("  Reason: " + result.getMessage());
        System.out.println();
    }
}
