package com.example.movieofficial.utils.services;

import com.example.movieofficial.config.VnPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VnPayService {

    private final HttpServletRequest request;

    private final VnPayConfig vnPayConfig;

    @Getter
    @Value("${vn_pay.bill_detail}")
    private String redirectBill;

    public String doPost(long cost, String id) {

        Map<String, String> vnp_Params = initParams(cost, id);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return vnPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    public String getIpAddress() {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }

    protected Map<String, String> initParams(long cost, String id) {
        var vnp_Version = "2.1.0";
        var vnp_Command = "pay";
        var orderType = "other";
        var amount = cost * 100L;
        var vnp_IpAddr = getIpAddress();
        var vnp_TmnCode = vnPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", id);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan ve xem phim:" + id);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, vnPayConfig.timeOut);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        return vnp_Params;
    }

    public String getMessage(String responseCode, String transactionStatus) {
        Map<String, String> responseCodeMessages = getStringStringMap();
        if (responseCodeMessages.containsKey(responseCode)) {
            return responseCodeMessages.get(transactionStatus);
        }
        if (transactionStatus.equals("01")) {
            return "Chưa thanh toán";
        } else {
            return "Transaction Status invalid";
        }
    }

    private Map<String, String> getStringStringMap() {
        var responseCodeMessages = new HashMap<String, String>();
        responseCodeMessages.put("09", "Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng");
        responseCodeMessages.put("10", "Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần");
        responseCodeMessages.put("11", "Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
        responseCodeMessages.put("12", "Thẻ/Tài khoản của khách hàng bị khóa.");
        responseCodeMessages.put("24", "Khách hàng hủy giao dịch.");
        responseCodeMessages.put("51", "Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
        responseCodeMessages.put("65", "Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
        responseCodeMessages.put("75", "Ngân hàng thanh toán đang bảo trì.");
        responseCodeMessages.put("79", "KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch.");
        responseCodeMessages.put("99", "Lỗi không xác định.");
        return responseCodeMessages;
    }
}
