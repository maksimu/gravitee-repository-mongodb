// "apis" collection
db.apis.dropIndexes();
db.apis.createIndex( { "visibility" : 1 } );
db.apis.createIndex( { "group" : 1 } );
db.apis.reIndex();

// "applications" collection
db.applications.dropIndexes();
db.applications.createIndex( { "group" : 1 } );
db.applications.createIndex( { "name" : 1 } );
db.applications.createIndex( { "status" : 1 } );
db.applications.reIndex();

// "events" collection
db.events.dropIndexes();
db.events.createIndex( { "type" : 1 } );
db.events.createIndex( { "updatedAt" : 1 } );
db.events.createIndex( { "properties.api_id" : 1 } );
db.events.createIndex( { "properties.api_id":1, "type":1} );
db.events.reIndex();

// "plans" collection
db.plans.dropIndexes();
db.plans.createIndex( { "apis" : 1 } );
db.plans.reIndex();

// "subscriptions" collection
db.subscriptions.dropIndexes();
db.subscriptions.createIndex( { "plan" : 1 } );
db.subscriptions.createIndex( { "application" : 1 } );
db.subscriptions.reIndex();

// "keys" collection
db.keys.dropIndexes();
db.keys.createIndex( { "plan" : 1 } );
db.keys.createIndex( { "application" : 1 } );
db.keys.createIndex( { "updatedAt" : 1 } );
db.keys.createIndex( { "revoked" : 1 } );
db.keys.createIndex( { "plan" : 1 , "revoked" : 1, "updatedAt" : 1 } );
db.keys.reIndex();

// "pages" collection
db.pages.dropIndexes();
db.pages.createIndex( { "api" : 1 } );
db.pages.reIndex();

// "memberships" collection
db.memberships.dropIndexes();
db.memberships.createIndex( {"_id.referenceId":1, "_id.referenceType":1} );
db.memberships.createIndex( {"_id.referenceId":1, "_id.referenceType":1, "roles":1} );
db.memberships.createIndex( {"_id.userId":1, "_id.referenceType":1} );
db.memberships.createIndex( {"_id.userId":1, "_id.referenceType":1, "roles":1} );
db.memberships.reIndex();

// "roles" collection
db.roles.dropIndexes();
db.roles.createIndex( {"_id.scope": 1 } );
db.roles.reIndex();

// "audits" collection
db.audits.dropIndexes();
db.audits.createIndex( { "referenceType": 1, "referenceId": 1 } );
db.audits.createIndex( { "createdAt": 1 } );
db.audits.reIndex();

// "rating" collection
db.rating.dropIndexes();
db.rating.createIndex( { "api" : 1 } );
db.rating.reIndex();

// "ratingAnswers" collection
db.ratingAnswers.dropIndexes();
db.ratingAnswers.createIndex( { "rating" : 1 } );

// "notifications" collection
db.notifications.dropIndexes();
db.notifications.createIndex( { "username" : 1 } );
db.notifications.reIndex();
