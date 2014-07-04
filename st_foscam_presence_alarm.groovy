/**
 *  Foscam Presence Alarm
 *
 *  Author: skp19
 *
 */
definition(
    name: "Foscam Presence Alarm",
    namespace: "skp19",
    author: "skp19",
    description: "Enables/disables Foscam alarm when people arrive or leave",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    section("Arm/Disarm these cameras") {
		input "cameras", "capability.imageCapture", multiple: true
		input "notify", "enum", title: "Notification?", metadata: [values: ["Yes","No"]]
		input "buttonMode", "enum", title: "Button Function", metadata: [values: ["Enable Alarm", "Disable Alarm"]]
	}
    
    section("When people arrive/depart") {
    	input "presence", "capability.presenceSensor", title: "Who?", multiple: true
    }
}

def installed() {
	subscribe(presence, "presence", presenceAlarm)
	subscribe(app, toggleAlarm)
}

def updated() {
	unsubscribe()
	subscribe(presence, "presence", presenceAlarm)
	subscribe(app, toggleAlarm)
}

def presenceAlarm(evt) {
	if (evt.value == "present") {
    	log.debug "${presence.label ?: presence.name} has arrived at ${location}"
    	cameras?.alarmOff()
        sendMessage("Foscam alarm disabled")
    }
    else {
    	def nobodyHome = presence.find{it.currentPresence == "present"} == null
        if (nobodyHome) {
			log.debug "Everyone has left ${location}"
			cameras?.alarmOn()
			sendMessage("Foscam alarm enabled")
        }
    }
}

def sendMessage(msg) {
	if (notify == "Yes") {
		sendPush msg
	}
}

def toggleAlarm(evt) {
	if (buttonMode == "Enable Alarm") {
    	log.debug "Alarms Enabled"
    	cameras?.alarmOn()
    }
    else {
        log.debug "Alarms Disabled"
    	cameras?.alarmOff()
    }
}
