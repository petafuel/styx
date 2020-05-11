DO
$$
    DECLARE
        tempUrlId      integer;
        spardaConfigId integer;
    BEGIN
        --insert new config with pre-step enabled
        INSERT INTO public.configs (standard_id, config, updated_at, created_at, styx_config)
        VALUES (2, '{
          "IO1": {
            "options": {
              "required": true
            },
            "description": "Mandate the TPP to sign requests on application level"
          },
          "IO2": {
            "options": {
              "sepa-credit-transfers": true,
              "instant-sepa-credit-transfers": true,
              "target-2-payments": false,
              "cross-border-credit-transfers": false,
              "pain.001-sepa-credit-transfers": false,
              "pain.001-instant-sepa-credit-transfers": false,
              "pain.001-target-2-payments": false,
              "pain.001-cross-border-credit-transfers": false
            },
            "description": "Support single payment products"
          },
          "IO3": {
            "options": {
              "sepa-credit-transfers": true,
              "instant-sepa-credit-transfers": true,
              "target-2-payments": false,
              "cross-border-credit-transfers": false,
              "pain.001-sepa-credit-transfers": false,
              "pain.001-instant-sepa-credit-transfers": false,
              "pain.001-target-2-payments": false,
              "pain.001-cross-border-credit-transfers": false
            },
            "description": "Support bulk payment products"
          },
          "IO4": {
            "options": {
              "sepa-credit-transfers": true,
              "instant-sepa-credit-transfers": true,
              "target-2-payments": false,
              "cross-border-credit-transfers": false,
              "pain.001-sepa-credit-transfers": false,
              "pain.001-instant-sepa-credit-transfers": false,
              "pain.001-target-2-payments": false,
              "pain.001-cross-border-credit-transfers": false
            },
            "description": "Support periodic payment products"
          },
          "IO5": {
            "options": {
              "redirect": false,
              "oauth": true,
              "decoupled": false,
              "embedded": true
            },
            "description": "Support SCA approaches"
          },
          "IO6": {
            "options": {
              "required": true
            },
            "description": "OAuth2 required as a pre-step for PSU authentication (of the first factor)"
          },
          "IO9": {
            "options": {
              "psu_authentication_required": true,
              "psu_identification_required": true,
              "authentication_method_selection_required": true,
              "no_requirements": true
            },
            "description": "Risk management regarding the offering of SCA methods via the XS2A interface"
          },
          "IO10": {
            "options": {
              "available": true
            },
            "description": "Transaction fees transported via the XS2A interface"
          },
          "IO11": {
            "options": {
              "sms_otp": true,
              "chip_otp": true,
              "photo_otp": true,
              "push_otp": true
            },
            "description": "Supported SCA Methods"
          },
          "IO12": {
            "options": {
              "available": true
            },
            "description": "Configuration of supported SCA methods - applicable SCA approaches"
          },
          "IO13": {
            "options": {
              "available": true
            },
            "description": "Configuration of supported SCA methods - TPP redirect preferred"
          },
          "IO14": {
            "options": {
              "identification_sufficient": true,
              "authentication_required": true
            },
            "description": "Authentication requirements for the decoupled SCA approach"
          },
          "IO15": {
            "options": {
              "payment_initiation_request": true,
              "account_information_consent_request": true,
              "payment_cancellation": true,
              "signing_basket": true
            },
            "description": "PSU-ID required in message"
          },
          "IO16": {
            "options": {
              "payment_initiation_request": true,
              "account_information_consent_request": true,
              "payment_cancellation": true,
              "signing_basket": true
            },
            "description": "PSU-ID-Type required in message"
          },
          "IO17": {
            "options": {
              "available": true
            },
            "description": "Support of multicurrency accounts"
          },
          "IO18": {
            "options": {
              "iban": true,
              "bban": false,
              "pan": false,
              "masked_pan": false,
              "msisdn": false
            },
            "description": "Representation of an account"
          },
          "IO19": {
            "options": {
              "payment_initiation_request": true,
              "account_information_consent_request": true,
              "payment_cancellation": true,
              "signing_basket": true
            },
            "description": "PSU-Corporate-ID required in message, if a corporate account is affected"
          },
          "IO20": {
            "options": {
              "payment_initiation_request": true,
              "account_information_consent_request": true,
              "payment_cancellation": true,
              "signing_basket": true
            },
            "description": "PSU-Corporate-ID-type required in message, if a corporate account is affected"
          },
          "IO21": {
            "options": {
              "available": true
            },
            "description": "Support of future dated payments"
          },
          "IO22": {
            "options": {
              "if_creditor_account_belongs_to_PSU": true,
              "if_creditor_is_on_a_whitelist_of_the_PSU": true,
              "if_instructed_amount_does_not_exceed_a_certain_limit": true,
              "transaction_risk_analyse": true
            },
            "description": "Support of SCA exemption"
          },
          "IO23": {
            "options": {
              "available": true
            },
            "description": "Support of sessions (combination of AIS and PIS)"
          },
          "IO24": {
            "options": {
              "decoupled_sca_initiated": true,
              "sca_method_chosen_embedded": true
            },
            "description": "Support of PSU messages in relevant scenarios"
          },
          "IO25": {
            "options": {
              "signing_baskets_for_the_same_payment_product_allowed_only_individual_payments": true,
              "signing_baskets_for_the_various_payment_products_allowed_only_individual_payments": true,
              "signing_baskets_for_the_same_payment_product_allowed_also_payments_with_multi_level_sca": true,
              "signing_baskets_for_the_various_payment_products_allowed_also_payments_with_multi_level_sca": true,
              "signing_baskets_for_payments_and_consent_establishment_allowed_only_individual_payments": true,
              "signing_baskets_for_payments_and_consent_establishment_allowed_also_payments_with_multi_level_sca": true
            },
            "description": "Grouping restrictions for signing baskets"
          },
          "IO26": {
            "options": {
              "required": true
            },
            "description": "SCA required for payment cancellation"
          },
          "IO27": {
            "options": {
              "payment_initiation_request": true,
              "account_information_consent_request": true,
              "payment_cancellation": true,
              "signing_basket": true
            },
            "description": "Multi level SCA supported for use cases"
          },
          "IO28": {
            "options": {
              "redirect": true,
              "embedded": true,
              "decoupled": true
            },
            "description": "Multi level SCA supported for use cases"
          },
          "IO29": {
            "options": {
              "redirect": true,
              "embedded": true,
              "decoupled": true
            },
            "description": "ASPSP enforces explicit start of authorisation"
          },
          "IO30": {
            "options": {
              "all_psd2_related_services_for_all_accounts": true,
              "only_access_right_in_request,_accounts_handled_between_psu_and_aspsp_afterwards": true,
              "list_of_available_accounts": true,
              "list_of_available_accounts_with_balances": true
            },
            "description": "Support of optional account Information access rights"
          },
          "IO31": {
            "options": {
              "camt.052": true,
              "camt.053": true,
              "camt.054": true,
              "json": true,
              "mt942": true,
              "mt940": true
            },
            "description": "Support of formats for account information"
          },
          "IO32": {
            "options": {
              "accounts_with_balance": true,
              "accounts_account-id_with_balance": true,
              "accounts_account-id_transactions_with_balance": true,
              "accounts_account-id_transactions_resourceId": true
            },
            "description": "Support of optional Endpoints for AIS"
          },
          "IO33": {
            "options": {
              "entry_reference_from": true,
              "booking_status_pending": true,
              "booking_status_both": true,
              "delta_list": true
            },
            "description": "Support of optional (values of) query parameters for AIS"
          },
          "IO34": {
            "options": {
              "opening_booked052": true,
              "expected": true,
              "interim_available": true,
              "forward_available": true,
              "non_invoiced": true,
              "closing_booked": true
            },
            "description": "Support of balance types"
          },
          "IO35": {
            "options": {
              "always": true,
              "never": true,
              "if_the_size_of_the_transaction_list_does_not_exceed_1_gb": true
            },
            "description": "Conditions for delivery of a transaction list directly in the response"
          },
          "IO36": {
            "options": {
              "always": true,
              "never": true,
              "if_the_size_of_the_transaction_list_does_exceed_1_gb": true
            },
            "description": "Conditions for delivery of a transaction list as a separate download with only a link in the response"
          },
          "IO37": {
            "options": {
              "available": true
            },
            "description": "Redirect after first SCA-Factor"
          },
          "IO38": {
            "options": {
              "available": true
            },
            "description": "Implicit start of transaction authorisation supported"
          },
          "IO39": {
            "options": {
              "available": true
            },
            "description": "API steering links of type ''''startAuthorisationWith...'''' supported (i.e. creation of authorisation sub-resources and delivery of missing data at the same time supported)"
          },
          "IO40": {
            "options": {
              "required": true
            },
            "description": "PSU authentication data delivered via the XS2A-interface (embedded approach) shall be encrypted at application level"
          },
          "IO41": {
            "options": {
              "aggregation_level": true,
              "sub-account_level": true
            },
            "description": "Access to multicurrency account details"
          },
          "IO42": {
            "options": {
              "available": true
            },
            "description": "Card number supported to identify subaccounts"
          },
          "IO43": {
            "options": {
              "payments_sepa-credit-transfers": true,
              "payments_instant-sepa-credit-transfers": true,
              "payments_target-2-payments": true,
              "payments_cross-border-credit-transfers": true,
              "payments_pain.001-sepa-credit-transfers": true,
              "payments_pain.001-instant-sepa-credit-transfers": true,
              "payments_pain.001-target-2-payments": true,
              "payments_pain.001-cross-border-credit-transfers": true,
              "bulk-payments_sepa-credit-transfers": true,
              "bulk-payments_instant-sepa-credit-transfers": true,
              "bulk-payments_target-2-payments": true,
              "bulk-payments_cross-border-credit-transfers": true,
              "bulk-payments_pain.001-sepa-credit-transfers": true,
              "bulk-payments_pain.001-instant-sepa-credit-transfers": true,
              "bulk-payments_pain.001-target-2-payments": true,
              "bulk-payments_pain.001-cross-border-credit-transfers": true,
              "periodic-payments_sepa-credit-transfers": true,
              "periodic-payments_instant-sepa-credit-transfers": true,
              "periodic-payments_target-2-payments": true,
              "periodic-payments_cross-border-credit-transfers": true,
              "periodic-payments_pain.001-sepa-credit-transfers": true,
              "periodic-payments_pain.001-instant-sepa-credit-transfers": true,
              "periodic-payments_pain.001-target-2-payments": true,
              "periodic-payments_pain.001-cross-border-credit-transfers": true
            },
            "description": "Support of payment cancellation per payment product"
          },
          "IO44": {
            "options": {
              "xml": true,
              "json": true
            },
            "description": "Supported formats of payment status response bodies for XML-based payments"
          },
          "IO45": {
            "options": {
              "batch_booking": true,
              "realtime_booking": true
            },
            "description": "Processing of regular (not instant) payments"
          },
          "IO46": {
            "options": {
              "available": true,
              "with_restrictions": true
            },
            "description": "Permission of requests for account data reading with PSU involvement and reference to a recurring consent."
          },
          "IO47": {
            "options": {
              "all": true,
              "cumulated": true
            },
            "description": "Counting the frequency of AIS requests"
          },
          "IO48": {
            "options": {
              "available": true
            },
            "description": "Endpoint signing-baskets/{basketId} supported"
          },
          "IO49": {
            "options": {
              "available": true
            },
            "description": "Endpoint signing-baskets/{basketId}/status supported"
          }
        }', now(), now(), '{
          "STYX01": {
            "options": {
              "required": true
            },
            "description": "ASPSP uses frequency names instead of frequency codes (ISO20022) for periodic payments"
          },
          "STYX02": {
            "options": {
              "required": false
            },
            "description": "PSU-ID needs to be extracted from the oauth pre-step access token"
          }
        }')
        RETURNING id INTO spardaConfigId;

        --Sparda-Bank Augsburg eG
        INSERT INTO public.urls (url, ais_url, pis_url, piis_url, updated_at, created_at,
                                 preauth_authorization_endpoint, preauth_token_endpoint)
        VALUES ('https://api.sparda.de:443/xs2a', null, null, null, now(), now(),
                'https://idp.sparda-a.de/oauth2/authorize', 'https://idp.sparda.de/oauth2/token')
        RETURNING id INTO tempUrlId;
        UPDATE public.aspsps SET production_url_id=tempUrlId, config_id=spardaConfigId WHERE bic = 'GENODEF1S03';

        --Sparda-Bank Baden-Württemberg eG
        INSERT INTO public.urls (url, ais_url, pis_url, piis_url, updated_at, created_at,
                                 preauth_authorization_endpoint, preauth_token_endpoint)
        VALUES ('https://api.sparda.de:443/xs2a', null, null, null, now(), now(),
                'https://idp.sparda-bw.de/oauth2/authorize', 'https://idp.sparda.de/oauth2/token')
        RETURNING id INTO tempUrlId;
        UPDATE public.aspsps SET production_url_id=tempUrlId, config_id=spardaConfigId WHERE bic = 'GENODEF1S02';

        --Sparda-Bank Hamburg eG
        INSERT INTO public.urls (url, ais_url, pis_url, piis_url, updated_at, created_at,
                                 preauth_authorization_endpoint, preauth_token_endpoint)
        VALUES ('https://api.sparda.de:443/xs2a', null, null, null, now(), now(),
                'https://idp.sparda-bank-hamburg.de/oauth2/authorize', 'https://idp.sparda.de/oauth2/token')
        RETURNING id INTO tempUrlId;
        UPDATE public.aspsps SET production_url_id=tempUrlId, config_id=spardaConfigId WHERE bic = 'GENODEF1S11';

        --Sparda-Bank Hannover eG
        INSERT INTO public.urls (url, ais_url, pis_url, piis_url, updated_at, created_at,
                                 preauth_authorization_endpoint, preauth_token_endpoint)
        VALUES ('https://api.sparda.de:443/xs2a', null, null, null, now(), now(),
                'https://idp.sparda-h.de/oauth2/authorize', 'https://idp.sparda.de/oauth2/token')
        RETURNING id INTO tempUrlId;
        UPDATE public.aspsps SET production_url_id=tempUrlId, config_id=spardaConfigId WHERE bic = 'GENODEF1S09';

        --Sparda-Bank Hessen eG
        INSERT INTO public.urls (url, ais_url, pis_url, piis_url, updated_at, created_at,
                                 preauth_authorization_endpoint, preauth_token_endpoint)
        VALUES ('https://api.sparda.de:443/xs2a', null, null, null, now(), now(),
                'https://idp.sparda-hessen.de/oauth2/authorize', 'https://idp.sparda.de/oauth2/token')
        RETURNING id INTO tempUrlId;
        UPDATE public.aspsps SET production_url_id=tempUrlId, config_id=spardaConfigId WHERE bic = 'GENODEF1S12';

        --Sparda-Bank München eG
        INSERT INTO public.urls (url, ais_url, pis_url, piis_url, updated_at, created_at,
                                 preauth_authorization_endpoint, preauth_token_endpoint)
        VALUES ('https://api.sparda.de:443/xs2a', null, null, null, now(), now(),
                'https://idp.sparda-m.de/oauth2/authorize', 'https://idp.sparda.de/oauth2/token')
        RETURNING id INTO tempUrlId;
        UPDATE public.aspsps SET production_url_id=tempUrlId, config_id=spardaConfigId WHERE bic = 'GENODEF1S04';

        --Sparda-Bank Nürnberg eG
        INSERT INTO public.urls (url, ais_url, pis_url, piis_url, updated_at, created_at,
                                 preauth_authorization_endpoint, preauth_token_endpoint)
        VALUES ('https://api.sparda.de:443/xs2a', null, null, null, now(), now(),
                'https://idp.sparda-n.de/oauth2/authorize', 'https://idp.sparda.de/oauth2/token')
        RETURNING id INTO tempUrlId;
        UPDATE public.aspsps SET production_url_id=tempUrlId, config_id=spardaConfigId WHERE bic = 'GENODEF1S06';

        --Sparda-Bank Ostbayern eG
        INSERT INTO public.urls (url, ais_url, pis_url, piis_url, updated_at, created_at,
                                 preauth_authorization_endpoint, preauth_token_endpoint)
        VALUES ('https://api.sparda.de:443/xs2a', null, null, null, now(), now(),
                'https://idp.sparda-ostbayern.de/oauth2/authorize', 'https://idp.sparda.de/oauth2/token')
        RETURNING id INTO tempUrlId;
        UPDATE public.aspsps SET production_url_id=tempUrlId, config_id=spardaConfigId WHERE bic = 'GENODEF1S05';

        --Sparda-Bank Südwest eG
        INSERT INTO public.urls (url, ais_url, pis_url, piis_url, updated_at, created_at,
                                 preauth_authorization_endpoint, preauth_token_endpoint)
        VALUES ('https://api.sparda.de:443/xs2a', null, null, null, now(), now(),
                'https://idp.sparda-sw.de/oauth2/authorize', 'https://idp.sparda.de/oauth2/token')
        RETURNING id INTO tempUrlId;
        UPDATE public.aspsps SET production_url_id=tempUrlId, config_id=spardaConfigId WHERE bic = 'GENODEF1S01';

        --Sparda-Bank West eG and Sparda-Bank Münster
        INSERT INTO public.urls (url, ais_url, pis_url, piis_url, updated_at, created_at,
                                 preauth_authorization_endpoint, preauth_token_endpoint)
        VALUES ('https://api.sparda.de:443/xs2a', null, null, null, now(), now(),
                'https://idp.sparda-west.de/oauth2/authorize', 'https://idp.sparda.de/oauth2/token')
        RETURNING id INTO tempUrlId;
        UPDATE public.aspsps
        SET production_url_id=tempUrlId,
            config_id=spardaConfigId
        WHERE bic IN ('GENODED1SPW', 'GENODED1SPE', 'GENODED1SPK', 'GENODEF1S08');

        --change the main config to not use pre-step
        INSERT INTO public.configs (id, standard_id, config, updated_at, created_at, styx_config)
        VALUES (1, 2, '{
          "IO1": {
            "options": {
              "required": true
            },
            "description": "Mandate the TPP to sign requests on application level"
          },
          "IO2": {
            "options": {
              "sepa-credit-transfers": true,
              "instant-sepa-credit-transfers": true,
              "target-2-payments": true,
              "cross-border-credit-transfers": true,
              "pain.001-sepa-credit-transfers": false,
              "pain.001-instant-sepa-credit-transfers": false,
              "pain.001-target-2-payments": false,
              "pain.001-cross-border-credit-transfers": false
            },
            "description": "Support single payment products"
          },
          "IO3": {
            "options": {
              "sepa-credit-transfers": true,
              "instant-sepa-credit-transfers": true,
              "target-2-payments": true,
              "cross-border-credit-transfers": true,
              "pain.001-sepa-credit-transfers": false,
              "pain.001-instant-sepa-credit-transfers": false,
              "pain.001-target-2-payments": false,
              "pain.001-cross-border-credit-transfers": false
            },
            "description": "Support bulk payment products"
          },
          "IO4": {
            "options": {
              "sepa-credit-transfers": true,
              "instant-sepa-credit-transfers": true,
              "target-2-payments": true,
              "cross-border-credit-transfers": true,
              "pain.001-sepa-credit-transfers": false,
              "pain.001-instant-sepa-credit-transfers": false,
              "pain.001-target-2-payments": false,
              "pain.001-cross-border-credit-transfers": false
            },
            "description": "Support periodic payment products"
          },
          "IO5": {
            "options": {
              "redirect": true,
              "oauth": true,
              "decoupled": true,
              "embedded": true
            },
            "description": "Support SCA approaches"
          },
          "IO6": {
            "options": {
              "required": false
            },
            "description": "OAuth2 required as a pre-step for PSU authentication (of the first factor)"
          },
          "IO9": {
            "options": {
              "psu_authentication_required": true,
              "psu_identification_required": true,
              "authentication_method_selection_required": true,
              "no_requirements": true
            },
            "description": "Risk management regarding the offering of SCA methods via the XS2A interface"
          },
          "IO10": {
            "options": {
              "available": true
            },
            "description": "Transaction fees transported via the XS2A interface"
          },
          "IO11": {
            "options": {
              "sms_otp": true,
              "chip_otp": true,
              "photo_otp": true,
              "push_otp": true
            },
            "description": "Supported SCA Methods"
          },
          "IO12": {
            "options": {
              "available": true
            },
            "description": "Configuration of supported SCA methods - applicable SCA approaches"
          },
          "IO13": {
            "options": {
              "available": true
            },
            "description": "Configuration of supported SCA methods - TPP redirect preferred"
          },
          "IO14": {
            "options": {
              "identification_sufficient": true,
              "authentication_required": true
            },
            "description": "Authentication requirements for the decoupled SCA approach"
          },
          "IO15": {
            "options": {
              "payment_initiation_request": true,
              "account_information_consent_request": true,
              "payment_cancellation": true,
              "signing_basket": true
            },
            "description": "PSU-ID required in message"
          },
          "IO16": {
            "options": {
              "payment_initiation_request": true,
              "account_information_consent_request": true,
              "payment_cancellation": true,
              "signing_basket": true
            },
            "description": "PSU-ID-Type required in message"
          },
          "IO17": {
            "options": {
              "available": true
            },
            "description": "Support of multicurrency accounts"
          },
          "IO18": {
            "options": {
              "iban": true,
              "bban": true,
              "pan": true,
              "masked_pan": true,
              "msisdn": true
            },
            "description": "Representation of an account"
          },
          "IO19": {
            "options": {
              "payment_initiation_request": true,
              "account_information_consent_request": true,
              "payment_cancellation": true,
              "signing_basket": true
            },
            "description": "PSU-Corporate-ID required in message, if a corporate account is affected"
          },
          "IO20": {
            "options": {
              "payment_initiation_request": true,
              "account_information_consent_request": true,
              "payment_cancellation": true,
              "signing_basket": true
            },
            "description": "PSU-Corporate-ID-type required in message, if a corporate account is affected"
          },
          "IO21": {
            "options": {
              "available": true
            },
            "description": "Support of future dated payments"
          },
          "IO22": {
            "options": {
              "if_creditor_account_belongs_to_PSU": true,
              "if_creditor_is_on_a_whitelist_of_the_PSU": true,
              "if_instructed_amount_does_not_exceed_a_certain_limit": true,
              "transaction_risk_analyse": true
            },
            "description": "Support of SCA exemption"
          },
          "IO23": {
            "options": {
              "available": true
            },
            "description": "Support of sessions (combination of AIS and PIS)"
          },
          "IO24": {
            "options": {
              "decoupled_sca_initiated": true,
              "sca_method_chosen_embedded": true
            },
            "description": "Support of PSU messages in relevant scenarios"
          },
          "IO25": {
            "options": {
              "signing_baskets_for_the_same_payment_product_allowed_only_individual_payments": true,
              "signing_baskets_for_the_various_payment_products_allowed_only_individual_payments": true,
              "signing_baskets_for_the_same_payment_product_allowed_also_payments_with_multi_level_sca": true,
              "signing_baskets_for_the_various_payment_products_allowed_also_payments_with_multi_level_sca": true,
              "signing_baskets_for_payments_and_consent_establishment_allowed_only_individual_payments": true,
              "signing_baskets_for_payments_and_consent_establishment_allowed_also_payments_with_multi_level_sca": true
            },
            "description": "Grouping restrictions for signing baskets"
          },
          "IO26": {
            "options": {
              "required": true
            },
            "description": "SCA required for payment cancellation"
          },
          "IO27": {
            "options": {
              "payment_initiation_request": true,
              "account_information_consent_request": true,
              "payment_cancellation": true,
              "signing_basket": true
            },
            "description": "Multi level SCA supported for use cases"
          },
          "IO28": {
            "options": {
              "redirect": true,
              "embedded": true,
              "decoupled": true
            },
            "description": "Multi level SCA supported for use cases"
          },
          "IO29": {
            "options": {
              "redirect": true,
              "embedded": true,
              "decoupled": true
            },
            "description": "ASPSP enforces explicit start of authorisation"
          },
          "IO30": {
            "options": {
              "all_psd2_related_services_for_all_accounts": true,
              "only_access_right_in_request,_accounts_handled_between_psu_and_aspsp_afterwards": true,
              "list_of_available_accounts": true,
              "list_of_available_accounts_with_balances": true
            },
            "description": "Support of optional account Information access rights"
          },
          "IO31": {
            "options": {
              "camt.052": true,
              "camt.053": true,
              "camt.054": true,
              "json": true,
              "mt942": true,
              "mt940": true
            },
            "description": "Support of formats for account information"
          },
          "IO32": {
            "options": {
              "accounts_with_balance": true,
              "accounts_account-id_with_balance": true,
              "accounts_account-id_transactions_with_balance": true,
              "accounts_account-id_transactions_resourceId": true
            },
            "description": "Support of optional Endpoints for AIS"
          },
          "IO33": {
            "options": {
              "entry_reference_from": true,
              "booking_status_pending": true,
              "booking_status_both": true,
              "delta_list": true
            },
            "description": "Support of optional (values of) query parameters for AIS"
          },
          "IO34": {
            "options": {
              "opening_booked052": true,
              "expected": true,
              "interim_available": true,
              "forward_available": true,
              "non_invoiced": true,
              "closing_booked": true
            },
            "description": "Support of balance types"
          },
          "IO35": {
            "options": {
              "always": true,
              "never": true,
              "if_the_size_of_the_transaction_list_does_not_exceed_1_gb": true
            },
            "description": "Conditions for delivery of a transaction list directly in the response"
          },
          "IO36": {
            "options": {
              "always": true,
              "never": true,
              "if_the_size_of_the_transaction_list_does_exceed_1_gb": true
            },
            "description": "Conditions for delivery of a transaction list as a separate download with only a link in the response"
          },
          "IO37": {
            "options": {
              "available": true
            },
            "description": "Redirect after first SCA-Factor"
          },
          "IO38": {
            "options": {
              "available": true
            },
            "description": "Implicit start of transaction authorisation supported"
          },
          "IO39": {
            "options": {
              "available": true
            },
            "description": "API steering links of type ''''startAuthorisationWith...'''' supported (i.e. creation of authorisation sub-resources and delivery of missing data at the same time supported)"
          },
          "IO40": {
            "options": {
              "required": true
            },
            "description": "PSU authentication data delivered via the XS2A-interface (embedded approach) shall be encrypted at application level"
          },
          "IO41": {
            "options": {
              "aggregation_level": true,
              "sub-account_level": true
            },
            "description": "Access to multicurrency account details"
          },
          "IO42": {
            "options": {
              "available": true
            },
            "description": "Card number supported to identify subaccounts"
          },
          "IO43": {
            "options": {
              "payments_sepa-credit-transfers": true,
              "payments_instant-sepa-credit-transfers": true,
              "payments_target-2-payments": true,
              "payments_cross-border-credit-transfers": true,
              "payments_pain.001-sepa-credit-transfers": true,
              "payments_pain.001-instant-sepa-credit-transfers": true,
              "payments_pain.001-target-2-payments": true,
              "payments_pain.001-cross-border-credit-transfers": true,
              "bulk-payments_sepa-credit-transfers": true,
              "bulk-payments_instant-sepa-credit-transfers": true,
              "bulk-payments_target-2-payments": true,
              "bulk-payments_cross-border-credit-transfers": true,
              "bulk-payments_pain.001-sepa-credit-transfers": true,
              "bulk-payments_pain.001-instant-sepa-credit-transfers": true,
              "bulk-payments_pain.001-target-2-payments": true,
              "bulk-payments_pain.001-cross-border-credit-transfers": true,
              "periodic-payments_sepa-credit-transfers": true,
              "periodic-payments_instant-sepa-credit-transfers": true,
              "periodic-payments_target-2-payments": true,
              "periodic-payments_cross-border-credit-transfers": true,
              "periodic-payments_pain.001-sepa-credit-transfers": true,
              "periodic-payments_pain.001-instant-sepa-credit-transfers": true,
              "periodic-payments_pain.001-target-2-payments": true,
              "periodic-payments_pain.001-cross-border-credit-transfers": true
            },
            "description": "Support of payment cancellation per payment product"
          },
          "IO44": {
            "options": {
              "xml": true,
              "json": true
            },
            "description": "Supported formats of payment status response bodies for XML-based payments"
          },
          "IO45": {
            "options": {
              "batch_booking": true,
              "realtime_booking": true
            },
            "description": "Processing of regular (not instant) payments"
          },
          "IO46": {
            "options": {
              "available": true,
              "with_restrictions": true
            },
            "description": "Permission of requests for account data reading with PSU involvement and reference to a recurring consent."
          },
          "IO47": {
            "options": {
              "all": true,
              "cumulated": true
            },
            "description": "Counting the frequency of AIS requests"
          },
          "IO48": {
            "options": {
              "available": true
            },
            "description": "Endpoint signing-baskets/{basketId} supported"
          },
          "IO49": {
            "options": {
              "available": true
            },
            "description": "Endpoint signing-baskets/{basketId}/status supported"
          }
        }', '2019-12-17 10:19:26.000000', '2019-12-17 10:19:27.000000', null);

    END
$$;

