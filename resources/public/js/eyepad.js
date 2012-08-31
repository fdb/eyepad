eyepad = {};
eyepad.currentCode = "";

eyepad.init = function(padId) {
	eyepad.padId = padId;
	eyepad.codeMirror = CodeMirror.fromTextArea(document.getElementById('code'));
	eyepad.currentCode = eyepad.codeMirror.getValue();
	eyepad.evaluate(eyepad.currentCode);
	setInterval(eyepad.check, 500);
}

eyepad.check = function() {
	var code = eyepad.codeMirror.getValue();
	if (code != eyepad.currentCode) {
		eyepad.currentCode = code; 
		eyepad.evaluate(code);
	}
}

eyepad.evaluate = function(code) {
	$.ajax({
		url: '/eval/' + eyepad.padId,
		data: {code: code},
		type: 'POST',
		success: function(data) {
			$('#result').html(data);
		},
		error: function(err, status) {
			$('#result').html('ERROR: ' + err + " stat " + status);
		}
	});
}