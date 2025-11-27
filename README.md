#!/usr/bin/env python3

import base64, hashlib, os, urllib.parse, json, argparse
import requests
from playwright.sync_api import sync_playwright

# =========================================
# PKCE Utilities
# =========================================
def generate_pkce():
    verifier = base64.urlsafe_b64encode(os.urandom(40)).decode().rstrip("=")
    challenge = base64.urlsafe_b64encode(
        hashlib.sha256(verifier.encode()).digest()
    ).decode().rstrip("=")
    return verifier, challenge


def decode_jwt(token):
    payload_part = token.split(".")[1]
    padded = payload_part + "==="[: (4 - len(payload_part) % 4) % 4]
    return json.loads(base64.urlsafe_b64decode(padded))


# =========================================
# Playwright Automation to Capture Auth Code
# =========================================
def get_auth_code(auth_url, redirect_uri):
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False)
        context = browser.new_context()

        page = context.new_page()

        auth_code_holder = {"code": None}

        # Capture the final redirect URL containing the ?code=
        def capture_request(request):
            if request.url.startswith(redirect_uri) and "code=" in request.url:
                auth_code_holder["code"] = urllib.parse.parse_qs(
                    urllib.parse.urlparse(request.url).query
                )["code"][0]

        context.on("request", capture_request)

        print("\n🔗 Opening browser for Azure login...\n")
        page.goto(auth_url)

        # Wait until we capture the authorization code
        while auth_code_holder["code"] is None:
            page.wait_for_timeout(200)

        browser.close()
        return auth_code_holder["code"]


# =========================================
# Exchange Authorization Code for Access Token
# =========================================
def get_access_token(tenant, client, redirect, verifier, code):
    token_url = f"https://login.microsoftonline.com/{tenant}/oauth2/v2.0/token"

    data = {
        "client_id": client,
        "grant_type": "authorization_code",
        "code": code,
        "redirect_uri": redirect,
        "code_verifier": verifier,
    }

    resp = requests.post(token_url, data=data).json()
    if "access_token" not in resp:
        raise Exception(f"Token request failed: {resp}")

    return resp["access_token"]


# =========================================
# Call APIM with Token
# =========================================
def call_apim(apim_url, token):
    headers = {"Authorization": f"Bearer {token}"}
    r = requests.get(apim_url, headers=headers)
    print(f"\n🌐 APIM Response: {r.status_code}\n{r.text}\n")
    return r


# =========================================
# CLI Logic
# =========================================
def main():
    parser = argparse.ArgumentParser(
        description="CLI Tool to authenticate via Azure AD (PKCE) and call an APIM-secured API"
    )

    parser.add_argument("--tenant", help="Azure Tenant ID")
    parser.add_argument("--client", help="Client (App Registration) ID")
    parser.add_argument("--server", help="Server/API App ID")
    parser.add_argument("--redirect", help="Redirect URI")
    parser.add_argument("--scope", help="Scope name (e.g. User.Api.Read)")
    parser.add_argument("--apim", help="APIM endpoint URL")

    args = parser.parse_args()

    # Interactive fallback
    tenant = args.tenant or input("Tenant ID: ").strip()
    client = args.client or input("Client App ID: ").strip()
    server = args.server or input("Server/API App ID: ").strip()
    redirect = args.redirect or input("Redirect URI: ").strip()
    scope = args.scope or input("Scope name (e.g., User.Api.Read): ").strip()
    apim_url = args.apim or input("APIM endpoint URL: ").strip()

    # PKCE Generation
    verifier, challenge = generate_pkce()
    print("\n🔐 Generated PKCE")
    print("Verifier:", verifier)
    print("Challenge:", challenge)

    # Build Authorization URL
    full_scope = f"api://{server}/{scope} openid profile offline_access"
    encoded_redirect = urllib.parse.quote(redirect)

    auth_url = (
        f"https://login.microsoftonline.com/{tenant}/oauth2/v2.0/authorize?"
        f"client_id={client}"
        f"&response_type=code"
        f"&redirect_uri={encoded_redirect}"
        f"&response_mode=query"
        f"&scope={urllib.parse.quote(full_scope)}"
        f"&code_challenge={challenge}"
        f"&code_challenge_method=S256"
    )

    print("\n🧭 Authorization URL:")
    print(auth_url)

    # Step 1: Acquire Authorization Code
    code = get_auth_code(auth_url, redirect)
    print("\n✔ Captured Authorization Code:", code)

    # Step 2: Exchange Code for Token
    token = get_access_token(tenant, client, redirect, verifier, code)

    print("\n🎫 Access Token (JWT):\n")
    print(token)

    with open("jwt_token.txt", "w") as f:
        f.write(token)

    print("\n💾 Saved token to jwt_token.txt")

    # Step 3: Decode JWT
    decoded = decode_jwt(token)
    print("\n🧩 Decoded JWT:")
    print(json.dumps(decoded, indent=2))

    # Step 4: Call APIM
    call_apim(apim_url, token)


if __name__ == "__main__":
    main()