package xyz.tcheeric.phoenixd.common.rest;

public interface Request<T extends Request.Param, U extends Response> {

    default U getResponse() {
        throw new UnsupportedOperationException("Implement getResponse in Request implementations");
    }

    interface Param {
        enum Kind {
            PATH, QUERY
        }

        default Kind getKind() {
            return Kind.PATH;
        }
    }
}