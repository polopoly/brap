package no.tornado.brap.common;

import java.io.Serializable;

/**
 * This marker-class is used to replace any first methdo argument
 * that contains an input stream, so that the request can be serialized
 * and the inputstream can be re-routed via the http outputstream.
 */
public class InputStreamArgumentPlaceholder implements Serializable {
}
