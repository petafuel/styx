package net.petafuel.styx.core.xs2a.entities;

public enum PurposeCode {
    BKDF,    //Bank Loan Delayed Draw Funding
    BKFE,    //BankLoanFees
    BKFM,    //BankLoanFundingMemo
    BKIP,    //BankLoanAccruedInterestPayment
    BKPP,    //BankLoanPrincipalPaydown
    CBLK,    //CardBulkClearing
    CDCB,    //CardPaymentwithCashBack
    CDCD,    //CashDisbursement
    CDCS,    //CashDisbursementwithSurcharging
    CDDP,    //CardDeferredPayment
    CDOC,    //OriginalCredit
    CDQC,    //QuasiCash
    ETUP,    //E-PurseTopUp
    FCOL,    //FeeCollection
    MTUP,    //MobileTopUp
    ACCT,    //AccountManagement
    CASH,    //CashManagementTransfer
    COLL,    //CollectionPayment
    CSDB,    //CashDisbursement
    DEPT,    //Deposit
    INTC,    //IntraCompanyPayment
    LIMA,    //LiquidityManagement
    NETT,    //Netting
    BFWD,    //BondForward
    CCIR,    //CrossCurrencyIRS
    CCPC,    //CCPClearedInitialMargin
    CCPM,    //CCPClearedVariationMargin
    CCSM,    //CCPClearedInitialMarginSegregatedCash
    CRDS,    //CreditDefaultSwap
    CRPR,    //CrossProduct
    CRSP,    //CreditSupport
    CRTL,    //CreditLine
    EQPT,    //EquityOption
    EQUS,    //EquitySwap
    EXPT,    //ExoticOption
    EXTD,    //ExchangeTradedDerivatives
    FIXI,    //FixedIncome
    FWBC,    //ForwardBrokerOwnedCashCollateral
    FWCC,    //ForwardClientOwnedCashCollateral
    FWSB,    //ForwardBrokerOwnedCashCollateralSegregated
    FWSC,    //ForwardClientOwnedSegregatedCashCollateral
    MARG,    //Dailymarginonlistedderivatives
    MBSB,    //MBSBrokerOwnedCashCollateral
    MBSC,    //MBSClientOwnedCashCollateral
    MGCC,    //FuturesInitialMargin
    MGSC,    //FuturesInitialMarginClientOwnedSegregatedCashCollateral
    OCCC,    //ClientownedOCCpledgedcollateral
    OPBC,    //OTCOptionBrokerownedCashcollateral
    OPCC,    //OTCOptionClientownedCashcollateral
    OPSB,    //OTCOptionBrokerOwnedSegregatedCashCollateral
    OPSC,    //OTCOptionClientOwnedCashSegregatedCashCollateral
    OPTN,    //FXOption
    OTCD,    //OTCDerivatives
    REPO,    //RepurchaseAgreement
    RPBC,    //Bi-lateralrepobrokerownedcollateral
    RPCC,    //Repoclientownedcollateral
    RPSB,    //Bi-lateralrepobrokerownedsegregatedcashcollateral
    RPSC,    //Bi-lateralRepoclientownedsegregatedcashcollateral
    RVPO,    //ReverseRepurchaseAgreement
    SBSC,    //SecuritiesBuySellSellBuyBack
    SCIE,    //SingleCurrencyIRSExotic
    SCIR,    //SingleCurrencyIRS
    SCRP,    //SecuritiesCrossProducts
    SHBC,    //BrokerownedcollateralShortSale
    SHCC,    //ClientownedcollateralShortSale
    SHSL,    //ShortSell
    SLEB,    //SecuritiesLendingAndBorrowing
    SLOA,    //SecuredLoan
    SWBC,    //SwapBrokerownedcashcollateral
    SWCC,    //SwapClientownedcashcollateral
    SWPT,    //Swaption
    SWSB,    //SwapsBrokerOwnedSegregatedCashCollateral
    SWSC,    //SwapsClientOwnedSegregatedCashCollateral
    TBAS,    //ToBeAnnounced
    TBBC,    //TBABrokerownedcashcollateral
    TBCC,    //TBAClientownedcashcollateral
    TRCP,    //TreasuryCrossProduct
    AGRT,    //AgriculturalTransfer
    AREN,    //AccountsReceivablesEntry
    BEXP,    //BusinessExpenses
    BOCE,    //BackOfficeConversionEntry
    COMC,    //CommercialPayment
    CPYR,    //Copyright
    GDDS,    //PurchaseSaleOfGoods
    GDSV,    //PurchaseSaleOfGoodsAndServices
    GSCB,    //PurchaseSaleOfGoodsAndServicesWithCashBack
    LICF,    //LicenseFee
    MP2B,    //MobileP2BPayment
    POPE,    //PointofPurchaseEntry
    ROYA,    //Royalties
    SCVE,    //PurchaseSaleOfServices
    SERV,    //ServiceCharges
    SUBS,    //Subscription
    SUPP,    //SupplierPayment
    TRAD,    //Commercial
    CHAR,    //CharityPayment
    COMT,    //ConsumerThirdPartyConsolidatedPayment
    MP2P,    //MobileP2PPayment
    ECPG,    //GuaranteedEPayment
    ECPR,    //EPaymentReturn
    ECPU,    //NonGuaranteedEPayment
    EPAY,    //Epayment
    CLPR,    //CarLoanPrincipalRepayment
    COMP,    //CompensationPayment
    DBTC,    //DebitCollectionPayment
    GOVI,    //GovernmentInsurance
    HLRP,    //HousingLoanRepayment
    HLST,    //HomeLoanSettlement
    INPC,    //InsurancePremiumCar
    INPR,    //InsurancePremiumRefund
    INSC,    //PaymentofInsuranceClaim
    INSU,    //InsurancePremium
    INTE,    //Interest
    LBRI,    //LaborInsurance
    LIFI,    //LifeInsurance
    LOAN,    //Loan
    LOAR,    //LoanRepayment
    PENO,    //PaymentBasedOnEnforcementOrder
    PPTI,    //PropertyInsurance
    RELG,    //RentalLeaseGeneral
    RINP,    //RecurringInstallmentPayment
    TRFD,    //TrustFund
    FORW,    //ForwardForeignExchange
    FXNT,    //ForeignExchangeRelatedNetting
    ADMG,    //AdministrativeManagement
    ADVA,    //AdvancePayment
    BCDM,    //BearerChequeDomestic
    BCFG,    //BearerChequeForeign
    BLDM,    //BuildingMaintenance
    BNET,    //BondForwardNetting
    CBFF,    //CapitalBuilding
    CBFR,    //CapitalBuildingRetirement
    CCRD,    //CreditCardPayment
    CDBL,    //CreditCardBill
    CFEE,    //CancellationFee
    CGDD,    //CardGeneratedDirectDebit
    CORT,    //TradeSettlementPayment
    COST,    //Costs
    CPKC,    //CarparkCharges
    DCRD,    //DebitCardPayment
    DSMT,    //PrintedOrderDisbursement
    DVPM,    //DeliverAgainstPayment
    EDUC,    //Education
    FACT,    //FactorUpdaterelatedpayment
    FAND,    //FinancialAidInCaseOfNaturalDisaster
    FCPM,    //LatePaymentofFees&Charges
    FEES,    //PaymentofFees
    GOVT,    //GovernmentPayment
    ICCP,    //IrrevocableCreditCardPayment
    IDCP,    //IrrevocableDebitCardPayment
    IHRP,    //InstalmentHirePurchaseAgreement
    INSM,    //Installment
    IVPT,    //InvoicePayment
    MCDM,    //MultiCurrenyChequeDomestic
    MCFG,    //MultiCurrenyChequeForeign
    MSVC,    //MultipleServiceTypes
    NOWS,    //NotOtherwiseSpecified
    OCDM,    //OrderChequeDomestic
    OCFG,    //OrderChequeForeign
    OFEE,    //OpeningFee
    OTHR,    //Other
    PADD,    //Preauthorizeddebit
    PTSP,    //PaymentTerms
    RCKE,    //Re-presentedCheckEntry
    RCPT,    //ReceiptPayment
    REBT,    //Rebate
    REFU,    //Refund
    RENT,    //Rent
    REOD,    //AccountOverdraftRepayment
    RIMB,    //Reimbursementofapreviouserroneoustransaction
    RPNT,    //Bi-lateralrepointernetnetting
    RRBN,    //RoundRobin
    RRCT,    //ReimbursementReceivedCreditTransfer
    RVPM,    //ReceiveAgainstPayment
    SLPI,    //PaymentSlipInstruction
    SPLT,    //Splitpayments
    STDY,    //Study
    TBAN,    //TBApair-offnetting
    TBIL,    //TelecommunicationsBill
    TCSC,    //TownCouncilServiceCharges
    TELI,    //Telephone-InitiatedTransaction
    TMPG,    //TMPGclaimpayment
    TPRI,    //TriPartyRepoInterest
    TPRP,    //Tri-partyReponetting
    TRNC,    //TruncatedPaymentSlip
    TRVC,    //TravellerCheque
    WEBI,    //Internet-InitiatedTransaction
    IPAY,    //InstantPayments
    IPCA,    //InstantPaymentscancellation
    IPDO,    //InstantPaymentsfordonations
    IPEA,    //InstantPaymentsinE-Commercewithoutaddressdata
    IPEC,    //InstantPaymentsinE-Commercewithaddressdata
    IPEW,    //InstantPaymentsinE-Commerce
    IPPS,    //InstantPaymentsatPOS
    IPRT,    //InstantPaymentsreturn
    IPU2,    //InstantPaymentsunattendedvendingmachinewith2FA
    IPUW,    //InstantPaymentsunattendedvendingmachinewithout2FA
    ANNI,    //Annuity
    CAFI,    //CustodianManagementfeeIn-house
    CFDI,    //CapitalfallingdueIn-house
    CMDT,    //CommodityTransfer
    DERI,    //Derivatives
    DIVD,    //Dividend
    FREX,    //ForeignExchange
    HEDG,    //Hedging
    INVS,    //Investment&Securities
    PRME,    //PreciousMetal
    SAVG,    //Savings
    SECU,    //Securities
    SEPI,    //SecuritiesPurchaseIn-house
    TREA,    //TreasuryPayment
    UNIT,    //UnitTrustPurchase
    FNET,    //FuturesNettingPayment
    FUTR,    //Futures
    ANTS,    //AnesthesiaServices
    CVCF,    //ConvalescentCareFacility
    DMEQ,    //DurableMedicaleEquipment
    DNTS,    //DentalServices
    HLTC,    //HomeHealthCare
    HLTI,    //HealthInsurance
    HSPC,    //HospitalCare
    ICRF,    //IntermediateCareFacility
    LTCF,    //LongTermCareFacility
    MAFC,    //MedicalAidFundContribution
    MARF,    //MedicalAidRefund
    MDCS,    //MedicalServices
    VIEW,    //VisionCare
    CDEP,    //Creditdefaulteventpayment
    SWFP,    //Swapcontractfinalpayment
    SWPP,    //Swapcontractpartialpayment
    SWRS,    //Swapcontractresetpayment
    SWUF,    //Swapcontractupfrontpayment
    ADCS,    //AdvisoryDonationCopyrightServices
    AEMP,    //ActiveEmploymentPolicy
    ALLW,    //Allowance
    ALMY,    //AlimonyPayment
    BBSC,    //BabyBonusScheme
    BECH,    //ChildBenefit
    BENE,    //UnemploymentDisabilityBenefit
    BONU,    //BonusPayment.
    CCHD,    //Cashcompensation,Helplessness,Disability
    COMM,    //Commission
    CSLP,    //CompanySocialLoanPaymentToBank
    GFRP,    //GuaranteeFundRightsPayment
    GVEA,    //AustrianGovernmentEmployeesCategoryA
    GVEB,    //AustrianGovernmentEmployeesCategoryB
    GVEC,    //AustrianGovernmentEmployeesCategoryC
    GVED,    //AustrianGovernmentEmployeesCategoryD
    GWLT,    //GovermentWarLegislationTransfer
    HREC,    //HousingRelatedContribution
    PAYR,    //Payroll
    PEFC,    //PensionFundContribution
    PENS,    //PensionPayment
    PRCP,    //PricePayment
    RHBS,    //RehabilitationSupport
    SALA,    //SalaryPayment
    SPSP,    //SalaryPensionSumPayment
    SSBE,    //SocialSecurityBenefit
    LBIN,    //LendingBuy-InNetting
    LCOL,    //LendingCashCollateralFreeMovement
    LFEE,    //LendingFees
    LMEQ,    //LendingEquitymarked-to-marketcashcollateral
    LMFI,    //LendingFixedIncomemarked-to-marketcashcollateral
    LMRK,    //Lendingunspecifiedtypeofmarked-to-marketcashcollateral
    LREB,    //Lendingrebatepayments
    LREV,    //LendingRevenuePayments
    LSFL,    //LendingClaimPayment
    ESTX,    //EstateTax
    FWLV,    //ForeignWorkerLevy
    GSTX,    //Goods&ServicesTax
    HSTX,    //HousingTax
    INTX,    //IncomeTax
    NITX,    //NetIncomeTax
    PTXP,    //PropertyTax
    RDTX,    //RoadTax
    TAXS,    //TaxPayment
    VATX,    //ValueAddedTaxPayment
    WHLD,    //WithHolding
    TAXR,    //TaxRefund
    B112,    //TrailerFeePayment
    BR12,    //TrailerFeeRebate
    TLRF,    //Non-USmutualfundtrailerfeepayment
    TLRR,    //Non-USmutualfundtrailerfeerebatepayment
    AIRB,    //Air
    BUSB,    //Bus
    FERB,    //Ferry
    RLWY,    //Railway
    TRPT,    //RoadPricing
    CBTV,    //CableTVBill
    ELEC,    //ElectricityBill
    ENRG,    //Energies
    GASB,    //GasBill
    NWCH,    //NetworkCharge
    NWCM,    //NetworkCommunication
    OTLC,    //OtherTelecomRelatedBill
    PHON,    //TelephoneBill
    UBIL,    //Utilities
    WTER    //WaterBill

}