package me.bytebeats.mns.meta;

import java.util.List;
import java.util.Map;

public class SearchStockResponse {
    private QuotationCodeTable QuotationCodeTable;

    public QuotationCodeTable getQuotationCodeTable() {
        return QuotationCodeTable;
    }

    public void setQuotationCodeTable(QuotationCodeTable quotationCodeTable) {
        this.QuotationCodeTable = quotationCodeTable;
    }

    public static class QuotationCodeTable {
        private List<Map<String, String>> Data;
        private int Status;
        private String Message;
        private int TotalCount;
        private String BizCode;
        private String BizMsg;

        public List<Map<String, String>> getData() {
            return Data;
        }

        public void setData(List<Map<String, String>> data) {
            this.Data = data;
        }

        public int getStatus() {
            return Status;
        }

        public void setStatus(int status) {
            this.Status = status;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            this.Message = message;
        }

        public int getTotalCount() {
            return TotalCount;
        }

        public void setTotalCount(int totalCount) {
            this.TotalCount = totalCount;
        }

        public String getBizCode() {
            return BizCode;
        }

        public void setBizCode(String bizCode) {
            this.BizCode = bizCode;
        }

        public String getBizMsg() {
            return BizMsg;
        }

        public void setBizMsg(String bizMsg) {
            this.BizMsg = bizMsg;
        }
    }
}
