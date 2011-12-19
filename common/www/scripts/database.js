var sysDB;

function nullDataHandler(transaction, results) { }

function fatalErrorCB(err) {
    showError("naturelocator.database.fatal");
    return true;
}

function nonFatalErrorCB(err) {
    log("Non-fatal error processing SQL: "+err.message);
    return false;
}

function initializeDB() {

    try {
        if(!window.openDatabase) {
    	    showError("naturelocator.database.notsupported");
            sysDB = null;
            return;
        } else {
            sysDB = window.openDatabase("naturelocator", "1.0", "Nature Locator DB", 5000000); 
        }
	} catch(e) {
	    showError("naturelocator.database.fatal");
	}

    var db = sysDB;

    // Uncomment to reset DB on every startup
    //resetDB(db);
    
    try{
        db.transaction(
            function(tx){
                tx.executeSql('CREATE TABLE CURRENT (id INTEGER PRIMARY KEY, timestamp INTEGER, litter NVARCHAR(10), damage INTEGER, location NVARCHAR(100), photoURI NVARCHAR(100))');
                // continue only if there wasn't a current table
                tx.executeSql('INSERT INTO CURRENT (id, timestamp, litter, damage, location, photoURI) VALUES (1, null, null, null, null, null)');
                tx.executeSql('CREATE TABLE IF NOT EXISTS ARCHIVE (id INTEGER PRIMARY KEY, timestamp INTEGER, litter NVARCHAR(10), damage INTEGER, location NVARCHAR(100), photoURI NVARCHAR(100), pending BOOLEAN)');
            }, function(err) { log("no DB init: " + err.message); }, function() { log("inited DB"); }
        );	
    } catch(e) {
	    showError("naturelocator.database.fatal");
    }

}

function resetDB(db) {

	try{
        db.transaction(
            function(tx){
              tx.executeSql('DROP TABLE IF EXISTS CURRENT');
              tx.executeSql('DROP TABLE IF EXISTS ARCHIVE');
            }, function(err) { log("no DB reset: " + err.message); }, function() { log("reset DB"); }
        );	
    } catch(e) {
	    showError("naturelocator.database.fatal");
    }

    window.localStorage.clear();

}

function storeLitter(litterState) {

	if (!sysDB) return;
	
	var db = sysDB;
	
	if (litterState != null) {
		litterState = '"' + litterState + '"';
	}
	
	db.transaction(
        function(tx){
            tx.executeSql('UPDATE CURRENT SET litter = ' + litterState + ' WHERE id = 1');
        }, fatalErrorCB, function() { log("stored litter"); }
    );
	
}

function storeDamage(damageState) {

	if (!sysDB) return;

	var db = sysDB;

	db.transaction(
        function(tx){
            tx.executeSql('UPDATE CURRENT SET damage = ' + damageState + ' WHERE id = 1');
        }, fatalErrorCB, function() { log("stored damage"); }
    );
	
}

function storeLocation(location) {

	if (!sysDB) return;
	
	var db = sysDB;

	if (location != null) {
		location = '"' + location + '"';
	}
	
	db.transaction(
        function(tx){
            tx.executeSql('UPDATE CURRENT SET location = ' + location + ' WHERE id = 1');
        }, fatalErrorCB, function() { log("stored location"); }
    );
	
}

function storePhoto(uri) {

	if (!sysDB) return;
	
	var ts_value = "strftime('%s','now', 'localtime')";

	if (uri == null) {
		ts_value = null;
	} else {
		uri = '"' + uri + '"';
	}

	var db = sysDB;
	db.transaction(
        function(tx){
            tx.executeSql('UPDATE CURRENT SET photoURI = ' + uri + ', timestamp = ' + ts_value + ' WHERE id = 1');
        }, fatalErrorCB, function() { log("stored photo"); }
    );
	
}

function getCurrent(callback) {
	
	if (!sysDB) return;
	
	var db = sysDB;
	
	db.transaction(
        function(tx) {
            tx.executeSql('SELECT * FROM CURRENT WHERE id=1', [],
                function(tx, results) {
            		var data = new Object();
            		if(results.rows.length != 0) {
            			data.litter = results.rows.item(0).litter;
            			data.damage = results.rows.item(0).damage;
            			data.photoURI = results.rows.item(0).photoURI;
            			data.location = results.rows.item(0).location;
            			data.timestamp = results.rows.item(0).timestamp;
                    }
            		callback(data);
                }, fatalErrorCB
            );
        }, fatalErrorCB
    );
    
}

function getArchive(id, callback) {
	
	if (!sysDB) return;
	
	var db = sysDB;
	
	db.transaction(
        function(tx) {
            tx.executeSql('SELECT * FROM ARCHIVE WHERE id=' + id, [],
                function(tx, results) {
            		var data = new Object();
            		if(results.rows.length != 0) {
            			data.litter = results.rows.item(0).litter;
            			data.damage = results.rows.item(0).damage;
            			data.photoURI = results.rows.item(0).photoURI;
            			data.location = results.rows.item(0).location;
            			data.timestamp = results.rows.item(0).timestamp;
                    }
            		callback(data);
                }, fatalErrorCB
            );
        }, fatalErrorCB
    );
    
}

function archivePending(id) {

	if (!sysDB) return;
	if (typeof id != 'number') return;
	var db = sysDB;
	db.transaction(
        function(tx){
            tx.executeSql('UPDATE ARCHIVE SET pending = 0 WHERE id = ' + id);
        }, fatalErrorCB, function() { log("archived pending id " + id); }
    );
	
}

//Switched off
//function deleteArchive(id) {
//    
//	if (!sysDB) return;
//	if (typeof id != 'number') return;
//	var db = sysDB;
//	db.transaction(
//                   function(tx){
//                   tx.executeSql('DELETE FROM ARCHIVE WHERE id = ' + id);
//                   }, fatalErrorCB, function() { log("deleted archive id " + id); }
//                   );
//	
//}

function deleteAllArchive() {
    
	if (!sysDB) return;
	var db = sysDB;
	db.transaction(
                   function(tx){
                   tx.executeSql('DELETE FROM ARCHIVE');
                   }, fatalErrorCB, function() { log("deleted archive"); }
                   );
	
}

//Switched off
//function deleteSent() {
//    
//	if (!sysDB) return;
//	var db = sysDB;
//	db.transaction(
//                   function(tx){
//                   tx.executeSql('DELETE FROM ARCHIVE WHERE pending = 0');
//                   }, fatalErrorCB, function() { log("deleted all sent records"); }
//                   );
//	
//}

function storeEmail(email) {
	if(email == null || $.trim(email).length == 0) {
		window.localStorage.removeItem('email');
	} else {
		window.localStorage.setItem('email', email);
	}
}

function getEmail() {

	return window.localStorage.getItem('email');

}

function getArchiveList(callback) {

	if (!sysDB) return;
	
	var db = sysDB;
	
	db.transaction(
        function(tx) {
            tx.executeSql('SELECT * FROM ARCHIVE ORDER BY timestamp DESC', [],
                function(tx, results) {
                    var len = results.rows.length;
                    var html = "";
                    for (var i=0; i<len; i++){
                    	var item = results.rows.item(i);
                    	html += "<li>";
                    	if(item.pending) {
// check send pending switched off                    		
//                    		html += "<a onclick='checkSendPending(" + item.id + ")' href='#'>";
                    		html += "<a onclick='checkClickSendPending(" + item.id + ")' href='#'>";
                    	}
                    	html += "<img class='archive-photo-image' src='" + item.photoURI + "'/>";
                    	html += '<span class="status">';
                    	if(!item.pending) {
                    		html += '<img src="themes/naturelocator/img/icon-tick.png" width="20" height="20" alt="" />Sent</span>';
                    	} else {
                    		html += '<img src="themes/naturelocator/img/icon-exclam.png" width="20" height="20" alt="" />Retry</span>';
                    	}
                    	html += '<span class="details">';
                    	html += getPrettyDate(item.timestamp * 1000) + '<br />';
                    	if(item.location.match("^-?\\d+\\.\\d+,-?\\d+\\.\\d+$")) {
                        	var coords = item.location.split(',');
                        	html += parseFloat(coords[0]).toFixed(2) + ', ' + parseFloat(coords[1]).toFixed(2) + '<br />';
                    	} else {
                    		var placename = item.location; 
                    		if(placename.length > 10) {
                    			placename = placename.substring(0,10) + '...';
                    		}
                    		html += placename + '<br />';
                    	}
                        var damage = item.damage;
                    	if(damage == -1) {
                    		damage = "Don't know";
                    	}
                    	html += 'Damage: ' + damage;
                    	html += '</span>';
                    	if(item.pending) {
                    		html += "</a>";
                    	}
                		html += "</li>";
                    }
                    callback(html);
                }, fatalErrorCB
            );
        }, fatalErrorCB
    );

}

function archive() {
	__stash(false);
}

function pend() {
	__stash(true);
}

function __stash(pending) {
	
	if (!sysDB) return;
	
	var db = sysDB;
	
	db.transaction(
        function(tx) {
            tx.executeSql('SELECT * FROM CURRENT WHERE id=1', [],
                function(tx, results) {
                    if(results.rows.length != 0) {
                        log("ID = " + results.rows.item(0).id + " timestamp =  " + results.rows.item(0).timestamp);
                        db.transaction(
                            function(tx) {
                                tx.executeSql('INSERT INTO ARCHIVE (timestamp, litter, damage, location, photoURI, pending) VALUES ('
                                + results.rows.item(0).timestamp + ','
                                + '"' + results.rows.item(0).litter + '"' + ','
                                + results.rows.item(0).damage + ','
                                + '"' + results.rows.item(0).location + '"' + ','
                                + '"' + results.rows.item(0).photoURI + '"' + ','
                                + (pending? 1 : 0) + ')');
                            }, fatalErrorCB, stashSuccessCB
                        );
                    }
                }, fatalErrorCB
            );
        }, fatalErrorCB
    );

}

function stashSuccessCB() {
	log("Stashed");
	reset();
}

function getPendingCount() {

	if (!sysDB) return;
	
	var db = sysDB;
	if(typeof db == 'undefined') return;
	
	db.transaction(
        function(tx) {
            tx.executeSql('SELECT count(*) FROM ARCHIVE WHERE pending=1', [],
                function(tx, results) {
                    updatePendingCountCB(results.rows.item(0)['count(*)']);
                }, fatalErrorCB
            );
        }, fatalErrorCB
    );

}