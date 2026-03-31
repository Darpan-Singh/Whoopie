package com.thrillathon.client.model

// This file was causing a recursive type alias error. 
// Since Event is already defined in the shared module under the same package, 
// this typealias is unnecessary.
