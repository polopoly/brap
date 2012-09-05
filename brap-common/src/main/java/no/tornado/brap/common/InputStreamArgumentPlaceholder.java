package no.tornado.brap.common;

import java.io.Serializable;

/**
 * This marker-class is used to replace any first method argument
 * that contains an input stream, so that the request can be serialized
 * and the InputStream can be re-routed via the http OutputStream.
 */
public class InputStreamArgumentPlaceholder implements Serializable {

    private static final long serialVersionUID = 5234317114656930524L;
}
