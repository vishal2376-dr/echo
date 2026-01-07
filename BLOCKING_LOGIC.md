# 🛡️ Echo: Blocking Detection Logic

This document details the technical implementation of network blocking detection in the Echo application. The system performs a sequential 4-stage analysis to identify network interference at different layers of the OSI model.

## 1. DNS Analysis (Layer 7/3)
**Objective:** Verify domain name resolution integrity.

*   **Implementation:** `InetAddress.getAllByName(domain)`
*   **Detection Vectors:**
    *   **DNS Poisoning / Sinkholing:**
        *   Checks if the returned IP maps to known sinkhole addresses, such as `127.0.0.1` (localhost) or `::1` (IPv6 localhost).
        *   *Note: `0.0.0.0` was historically checked but removed to reduce false positives in specific network configurations.*
    *   **NXDOMAIN / Resolution Failure:**
        *   Captures `UnknownHostException`. If immediate, usually entails a DNS request blocking or non-existent domain.
    *   **Resolution Latency:**
        *   Measures time taken for resolution. Extremely fast responses (<5ms) for remote domains often indicate local DNS caching or interception.

## 2. TCP Handshake (Layer 4)
**Objective:** Verify basic connectivity to the host server.

*   **Implementation:** `java.net.Socket` with distinct checks for Port 443 (HTTPS) and Port 80 (HTTP).
*   **Detection Vectors:**
    *   **Port Blocking (Firewall):**
        *   Attempts `socket.connect(address, timeout)`.
        *   **Connection Refused (`ConnectException`):** The server or an upstream firewall actively rejected the packet (RST flag).
        *   **Connection Timeout (`SocketTimeoutException`):** Packets were dropped silently (DROP action), highly indicative of stateful firewall blocking.
    *   **Traffic Shaping:**
        *   Measures TCP handshake latency. Abnormally high latency on a "successful" connection can indicate traffic throttlers.

## 3. SSL/TLS Verification (Layer 6)
**Objective:** Validate the integrity of the encrypted channel (detect MITM).

*   **Implementation:** `HttpsURLConnection` (which handles the SSL handshake).
*   **Detection Vectors:**
    *   **Man-In-The-Middle (MITM) Attacks:**
        *   Inspects the server's certificate chain (`connection.serverCertificates`).
        *   **Self-Signed Certificates:** Checks if the certificate is not signed by a trusted CA (`Exception: CertPathValidatorException`).
        *   **Issuer Mismatch:** Analyzes the `IssuerDN`. Unexpected issuers (e.g., "Fortinet", "Cisco Umbrella", "Government CA") on a public domain like `google.com` confirm an intercepted connection.
    *   **Protocol Downgrade:**
        *   Checks the negotiated protocol version (e.g., forcing TLSv1.1 instead of TLSv1.3).

## 4. HTTP Layer (Layer 7)
**Objective:** Verify application-level content delivery.

*   **Implementation:** `HttpURLConnection.getResponseCode()` and Header analysis.
*   **Detection Vectors:**
    *   **Application Blocking (HTTP 403/451):**
        *   explicit blocking responses (e.g. 451 Unavailable For Legal Reasons).
    *   **Deep Packet Inspection (DPI):**
        *   If the TCP handshake succeeds but the HTTP GET request times out or is reset immediately after sending headers, it indicates a DPI system filtering based on the Host header (SNI blocking) or URL content.
    *   **Header Injection:**
        *   Scans response headers for known blocking notifications (e.g., `X-Blocked-By`, `X-Firewall`).

---

## Status Mapping Summary

| Internal State | UI Status | Description |
| :--- | :--- | :--- |
| `UnknownHostException` / Sinkhole IP | **DNS_BLOCKED** | Domain resolution failed or was redirected to localhost. |
| Port 443 OR Port 80 Unreachable | **FIREWALL_BLOCKED** | TCP handshake failed (Refused or Timeout). |
| SSL Handshake Failure / Bad Cert | **SSL_BLOCKED** | Certificate chain validation failed (MITM detected). |
| HTTP 4xx/5xx / Read Timeout | **HTTPS_BLOCKED** | Connection OK, but server/gateway refused content. |
