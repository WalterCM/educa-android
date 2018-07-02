package com.instituto.educa;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public final class APIEduca {
    private APIEduca() {}

    public static boolean isProfessor(String base64Encoded) {
        boolean isProf = false;
        try {
            JwtParser parser = Jwts.parser() .setSigningKey(APIContract.SECRET_KEY.getBytes("UTF-8"));
            isProf = parser.parseClaimsJws(base64Encoded).getBody().get("is_professor", Boolean.class);
        } catch (SignatureException e) {
            //don't trust the JWT!
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return isProf;
    }

    public static String getUrl(String path) {
        URI uri = null;
        try {
            uri = new URI(APIContract.URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        assert uri != null;
        return uri.resolve(path).toString();
    }
}
