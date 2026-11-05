import { HttpInterceptorFn } from "@angular/common/http";
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const credentials = sessionStorage.getItem("demoBasicAuth");
  return next(
    credentials
      ? req.clone({ setHeaders: { Authorization: `Basic ${credentials}` } })
      : req,
  );
};
