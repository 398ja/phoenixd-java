package xyz.tcheeric.phoenixd.common;

public interface Request<T extends Request.Param, U extends Response> {

    default U getResponse() {
        return null;
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