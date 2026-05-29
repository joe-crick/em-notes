import { sdef, shape_, or_, literal_, NonEmptyStr, Any } from "ljspec";
import { optional_ } from "./helpers.js";

// API error payload (plan §11). `details` is freeform.
export const ApiError = sdef(
  "ApiError",
  shape_({
    code: NonEmptyStr,
    message: NonEmptyStr,
    details: optional_(Any),
  })
);

// Success / failure envelopes.
export const ApiOk = sdef("ApiOk", shape_({ ok: literal_(true), data: Any }));
export const ApiErr = sdef("ApiErr", shape_({ ok: literal_(false), error: ApiError }));

// Either envelope.
export const ApiResult = sdef("ApiResult", or_(ApiOk, ApiErr));
