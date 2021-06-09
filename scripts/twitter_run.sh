#!/bin/bash
#IS_RUNNING=`docker-compose -p skupno_restore ps`
#if [[ "$IS_RUNNING" != "" ]]; then
  curl -X PUT -H "Content-Type: application/json" -d '{
    "status": {
      "runStatus": "RUNNING"
    },
    "component": {
      "state": "RUNNING",
      "id": "37466983-0178-1000-5779-621f9875d1f9"
    },
    "id": "37466983-0178-1000-5779-621f9875d1f9",
    "revision": {
      "version": 1,
      "clientId": "b60040c2-0179-1000-075f-650d494370ef"
    }
  } ' http://localhost:8080/nifi-api/processors/37466983-0178-1000-5779-621f9875d1f9

  sleep 20

  curl -X PUT -H "Content-Type: application/json" -d '{
    "status": {
      "runStatus": "STOPPED"
    },
    "component": {
      "state": "STOPPED",
      "id": "37466983-0178-1000-5779-621f9875d1f9"
    },
    "id": "37466983-0178-1000-5779-621f9875d1f9",
    "revision": {
      "version": 1,
      "clientId": "b60040c2-0179-1000-075f-650d494370ef"
    }
  } ' http://localhost:8080/nifi-api/processors/37466983-0178-1000-5779-621f9875d1f9

#fi
