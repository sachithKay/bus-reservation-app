# Security Implementation Plan

This document outlines the proposed security mechanisms for the Bus Ticket Reservation REST API, as required by the project specifications.

## 1. Authentication
To secure the Bus Ticket Reservation system, we propose **OAuth 2.0** as the primary standard for a professional, production-grade deployment.

### 1.1 Primary Recommendation: OAuth 2.0 (Client Credentials)
For an API that may eventually be exposed to third-party travel agencies or mobile applications, OAuth 2.0 provides the most robust security framework.

- **Mechanism**: Use the **Client Credentials Flow** where external services exchange a `client_id` and `client_secret` for a short-lived **JWT (JSON Web Token)**.
- **Validation**: The server validates the signature of the JWT on every request, ensuring the token hasn't expired or been tampered with.

### 1.2 Alternative: API Key Authentication
If the API is mostly machine facing, we can use an API key security implementation.

- **Mechanism**: A custom `SecurityFilter` intercepts requests to `/api/*` and checks for a valid key in the `X-API-KEY` HTTP header.
- **Implementation (Simplified)**:
    ```java
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        String apiKey = ((HttpServletRequest) request).getHeader("X-API-KEY");
        if (VALID_KEYS.contains(apiKey)) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).sendError(401, "Invalid API Key");
        }
    }
    ```

## 2. Rate Limiting 
1. Further more, if we want to ensure fair usage of API's, protecting againsnt DoS and throttling we can implement a **Token bucket** implem.
2. To further protect the application from attacks and resource limitations, we could implement a **Leaky Bucket** algorithm. But this would result in us losing bursts.

## 3. Data Protection (TLS)
While the API handles non-sensitive seat data, it should be deployed behind a reverse proxy (like Nginx) or a load balancer that terminates **TLS (HTTPS)**. This protects against man-in-the-middle (MITM) attacks during the simulated "payment collected" phase.
