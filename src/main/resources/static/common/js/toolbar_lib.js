document.write('<script src="/common/plugin/toolbar/js/Toolbar.js" type="text/javascript"></script>')


function getRoleProperty(ppValue, value) {
	if ((ppValue & value) == value) {
		return "T";
	} else {
		return "F";
	}
}