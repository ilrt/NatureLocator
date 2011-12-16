get  "/users",						forward:  "/listUsers.groovy"
get  "/users/deletevalidations/@name",				forward:  "/deleteUserValidations.groovy?name=@name"
get  "/users/delete/@name",				forward:  "/deleteUser.groovy?name=@name"

// basic report CRUD
get  "/reports",						forward:  "/listReports.groovy",				cache: 5.minutes
get  "/reportswl",						forward:  "/listWhiteReports.groovy",				cache: 5.minutes
get  "/reports/add",						forward:  "/WEB-INF/pages/reportAdd.gtpl"
post "/reports/insert",					forward:  "/insertReport.groovy"
get  "/reports/delete/@id",				forward:  "/deleteReport.groovy?id=@id"
get  "/reports/edit/@id",					forward:  "/editReport.groovy?id=@id"

// image handling
get  "/image/thumb/@id",				forward:  "/image.groovy?id=@id&thumb=true",	cache: 168.hours
get  "/image/@id",						forward:  "/image.groovy?id=@id",				cache: 168.hours

get  "/csvdump",						forward:  "/csvDump.groovy",					cache: 24.hours
get  "/csvdump/@size/@page",			forward:  "/csvDump.groovy?size=@size&page=@page",	cache: 24.hours
get  "/validationcsvdump",						forward:  "/csvValidationDump.groovy",					cache: 24.hours
get  "/validationcsvdump/@size/@page",			forward:  "/csvValidationDump.groovy?size=@size&page=@page",	cache: 24.hours
get  "/reports/export",					forward:  "/exportReports.groovy"
get  "/reports/deleteall",					forward:  "/deleteAllReports.groovy",			cache: 0.hours

// crowd validation
get "/validate",				forward:	"/validateReports.groovy",			cache: 0.hours
post "/validate",				forward:	"/validateReports.groovy",			cache: 0.hours

get "/results",								forward: "/showReportLocations.groovy",			cache: 168.hours

// need to place last for other rules to have effect
get  "/reports/@id",						forward:  "/viewReport.groovy?id=@id",			cache: 168.hours

// clear all memcaches
get  "/clearcaches",						forward:  "/clearCache.groovy"
