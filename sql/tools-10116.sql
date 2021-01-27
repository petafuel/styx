-- update Sparda base-URLs
UPDATE urls
SET url = 'https://api.sparda.de/xs2a/2.0.0', updated_at = now()
WHERE url = 'https://api.sparda.de:443/xs2a';

-- update Sparda configs/Implementer Options
 -- IO5 from redirect-only to oauth-only
 -- IO6 pre-step required from true to false
UPDATE configs
SET config = '{
"IO1": {"options": {"required": true}, "description": "Mandate the TPP to sign requests on application level"},
"IO2": {"options": {"target-2-payments": false, "sepa-credit-transfers": true, "pain.001-target-2-payments": false, "cross-border-credit-transfers": false, "instant-sepa-credit-transfers": false, "pain.001-sepa-credit-transfers": false, "pain.001-cross-border-credit-transfers": false, "pain.001-instant-sepa-credit-transfers": false}, "description": "Support single payment products"},
"IO3": {"options": {"target-2-payments": false, "sepa-credit-transfers": false, "pain.001-target-2-payments": false, "cross-border-credit-transfers": false, "instant-sepa-credit-transfers": false, "pain.001-sepa-credit-transfers": false, "pain.001-cross-border-credit-transfers": false, "pain.001-instant-sepa-credit-transfers": false}, "description": "Support bulk payment products"},
"IO4": {"options": {"target-2-payments": false, "sepa-credit-transfers": true, "pain.001-target-2-payments": false, "cross-border-credit-transfers": false, "instant-sepa-credit-transfers": false, "pain.001-sepa-credit-transfers": false, "pain.001-cross-border-credit-transfers": false, "pain.001-instant-sepa-credit-transfers": false}, "description": "Support periodic payment products"},
"IO5": {"options": {"oauth": true, "embedded": false, "redirect": false, "decoupled": false}, "description": "Support SCA approaches"},
"IO6": {"options": {"required": false}, "description": "OAuth2 required as a pre-step for PSU authentication (of the first factor)"},
"IO9": {"options": {"no_requirements": true, "psu_authentication_required": true, "psu_identification_required": true, "authentication_method_selection_required": true}, "description": "Risk management regarding the offering of SCA methods via the XS2A interface"},
"IO10": {"options": {"available": true}, "description": "Transaction fees transported via the XS2A interface"},
"IO11": {"options": {"sms_otp": true, "chip_otp": true, "push_otp": true, "photo_otp": true}, "description": "Supported SCA Methods"},
"IO12": {"options": {"available": true}, "description": "Configuration of supported SCA methods - applicable SCA approaches"},
"IO13": {"options": {"available": true}, "description": "Configuration of supported SCA methods - TPP redirect preferred"},
"IO14": {"options": {"authentication_required": true, "identification_sufficient": true}, "description": "Authentication requirements for the decoupled SCA approach"},
"IO15": {"options": {"signing_basket": true, "payment_cancellation": true, "payment_initiation_request": true, "account_information_consent_request": true}, "description": "PSU-ID required in message"},
"IO16": {"options": {"signing_basket": true, "payment_cancellation": true, "payment_initiation_request": true, "account_information_consent_request": true}, "description": "PSU-ID-Type required in message"},
"IO17": {"options": {"available": true}, "description": "Support of multicurrency accounts"},
"IO18": {"options": {"pan": false, "bban": false, "iban": true, "msisdn": false, "masked_pan": false}, "description": "Representation of an account"},
"IO19": {"options": {"signing_basket": true, "payment_cancellation": true, "payment_initiation_request": true, "account_information_consent_request": true}, "description": "PSU-Corporate-ID required in message, if a corporate account is affected"},
"IO20": {"options": {"signing_basket": true, "payment_cancellation": true, "payment_initiation_request": true, "account_information_consent_request": true}, "description": "PSU-Corporate-ID-type required in message, if a corporate account is affected"},
"IO21": {"options": {"available": true}, "description": "Support of future dated payments"},
"IO22": {"options": {"transaction_risk_analyse": true, "if_creditor_account_belongs_to_PSU": true, "if_creditor_is_on_a_whitelist_of_the_PSU": true, "if_instructed_amount_does_not_exceed_a_certain_limit": true}, "description": "Support of SCA exemption"},
"IO23": {"options": {"available": true}, "description": "Support of sessions (combination of AIS and PIS)"},
"IO24": {"options": {"decoupled_sca_initiated": true, "sca_method_chosen_embedded": true}, "description": "Support of PSU messages in relevant scenarios"},
"IO25": {"options": {"signing_baskets_for_the_same_payment_product_allowed_only_individual_payments": true, "signing_baskets_for_the_various_payment_products_allowed_only_individual_payments": true, "signing_baskets_for_payments_and_consent_establishment_allowed_only_individual_payments": true, "signing_baskets_for_the_same_payment_product_allowed_also_payments_with_multi_level_sca": true, "signing_baskets_for_the_various_payment_products_allowed_also_payments_with_multi_level_sca": true, "signing_baskets_for_payments_and_consent_establishment_allowed_also_payments_with_multi_level_sca": true}, "description": "Grouping restrictions for signing baskets"}, "IO26": {"options": {"required": true}, "description": "SCA required for payment cancellation"},
"IO27": {"options": {"signing_basket": true, "payment_cancellation": true, "payment_initiation_request": true, "account_information_consent_request": true}, "description": "Multi level SCA supported for use cases"},
"IO28": {"options": {"embedded": true, "redirect": true, "decoupled": true}, "description": "Multi level SCA supported for use cases"},
"IO29": {"options": {"embedded": true, "redirect": true, "decoupled": true}, "description": "ASPSP enforces explicit start of authorisation"},
"IO30": {"options": {"list_of_available_accounts": true, "list_of_available_accounts_with_balances": true, "all_psd2_related_services_for_all_accounts": true, "only_access_right_in_request,_accounts_handled_between_psu_and_aspsp_afterwards": true}, "description": "Support of optional account Information access rights"},
"IO31": {"options": {"json": true, "mt940": true, "mt942": true, "camt.052": true, "camt.053": true, "camt.054": true}, "description": "Support of formats for account information"},
"IO32": {"options": {"accounts_with_balance": true, "accounts_account-id_with_balance": true, "accounts_account-id_transactions_resourceId": true, "accounts_account-id_transactions_with_balance": true}, "description": "Support of optional Endpoints for AIS"},
"IO33": {"options": {"delta_list": true, "booking_status_both": true, "entry_reference_from": true, "booking_status_pending": true}, "description": "Support of optional (values of) query parameters for AIS"},
"IO34": {"options": {"expected": true, "non_invoiced": true, "closing_booked": true, "forward_available": true, "interim_available": true, "opening_booked052": true}, "description": "Support of balance types"},
"IO35": {"options": {"never": true, "always": true, "if_the_size_of_the_transaction_list_does_not_exceed_1_gb": true}, "description": "Conditions for delivery of a transaction list directly in the response"},
"IO36": {"options": {"never": true, "always": true, "if_the_size_of_the_transaction_list_does_exceed_1_gb": true}, "description": "Conditions for delivery of a transaction list as a separate download with only a link in the response"},
"IO37": {"options": {"available": true}, "description": "Redirect after first SCA-Factor"},
"IO38": {"options": {"available": true}, "description": "Implicit start of transaction authorisation supported"},
"IO39": {"options": {"available": true}, "description": "API steering links of type ''''startAuthorisationWith...'''' supported (i.e. creation of authorisation sub-resources and delivery of missing data at the same time supported)"},
"IO40": {"options": {"required": true}, "description": "PSU authentication data delivered via the XS2A-interface (embedded approach) shall be encrypted at application level"},
"IO41": {"options": {"aggregation_level": true, "sub-account_level": true}, "description": "Access to multicurrency account details"},
"IO42": {"options": {"available": true}, "description": "Card number supported to identify subaccounts"},
"IO43": {"options": {"payments_target-2-payments": true, "payments_sepa-credit-transfers": true, "bulk-payments_target-2-payments": true, "bulk-payments_sepa-credit-transfers": true, "payments_pain.001-target-2-payments": true, "periodic-payments_target-2-payments": true, "payments_cross-border-credit-transfers": true, "payments_instant-sepa-credit-transfers": true, "payments_pain.001-sepa-credit-transfers": true, "periodic-payments_sepa-credit-transfers": true, "bulk-payments_pain.001-target-2-payments": true, "bulk-payments_cross-border-credit-transfers": true, "bulk-payments_instant-sepa-credit-transfers": true, "bulk-payments_pain.001-sepa-credit-transfers": true, "periodic-payments_pain.001-target-2-payments": true, "payments_pain.001-cross-border-credit-transfers": true, "payments_pain.001-instant-sepa-credit-transfers": true, "periodic-payments_cross-border-credit-transfers": true, "periodic-payments_instant-sepa-credit-transfers": true, "periodic-payments_pain.001-sepa-credit-transfers": true, "bulk-payments_pain.001-cross-border-credit-transfers": true, "bulk-payments_pain.001-instant-sepa-credit-transfers": true, "periodic-payments_pain.001-cross-border-credit-transfers": true, "periodic-payments_pain.001-instant-sepa-credit-transfers": true}, "description": "Support of payment cancellation per payment product"},
"IO44": {"options": {"xml": true, "json": true}, "description": "Supported formats of payment status response bodies for XML-based payments"},
"IO45": {"options": {"batch_booking": true, "realtime_booking": true}, "description": "Processing of regular (not instant) payments"},
"IO46": {"options": {"available": true, "with_restrictions": true}, "description": "Permission of requests for account data reading with PSU involvement and reference to a recurring consent."},
"IO47": {"options": {"all": true, "cumulated": true}, "description": "Counting the frequency of AIS requests"},
"IO48": {"options": {"available": true}, "description": "Endpoint signing-baskets/{basketId} supported"},
"IO49": {"options": {"available": true}, "description": "Endpoint signing-baskets/{basketId}/status supported"}
}',
updated_at = now()
WHERE id IN (SELECT config_id FROM aspsps WHERE name ILIKE '%sparda%');
