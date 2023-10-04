package org.folio.ed.utils;

import org.folio.ed.domain.dto.DcbItem;
import org.folio.ed.domain.dto.DcbPatron;
import org.folio.ed.domain.dto.DcbTransaction;
import org.folio.ed.domain.dto.TransactionStatus;
import org.folio.ed.domain.dto.TransactionStatusResponse;

public class EntityUtils {

  public static String ITEM_ID = "5b95877d-86c0-4cb7-a0cd-7660b348ae5a";
  public static String PATRON_ID = "571b0a2c-9456-40b5-a449-d41fe6017082";

  public static DcbTransaction createDcbTransaction() {
    return DcbTransaction.builder()
      .item(createDcbItem())
      .patron(createDcbPatron())
      .role(DcbTransaction.RoleEnum.LENDER)
      .build();
  }

  public static TransactionStatus createTransactionStatus(TransactionStatus.StatusEnum statusEnum){
    return TransactionStatus.builder().status(statusEnum).build();
  }

  public static DcbItem createDcbItem() {
    return DcbItem.builder()
      .id(ITEM_ID)
      .barcode("DCB_ITEM")
      .title("ITEM")
      .lendingLibraryCode("KU")
      .pickupLocation("Datalogisk Institute")
      .materialType("book")
      .build();
  }

  public static DcbPatron createDcbPatron() {
    return DcbPatron.builder()
      .id(PATRON_ID)
      .barcode("DCB_PATRON")
      .group("staff")
      .borrowingLibraryCode("E")
      .build();
  }

  public static TransactionStatusResponse createTransactionStatusResponse(TransactionStatusResponse.StatusEnum statusEnum){
    return TransactionStatusResponse.builder().status(statusEnum).build();
  }
}
