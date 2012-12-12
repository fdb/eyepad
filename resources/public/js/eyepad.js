eyepad = {};
eyepad.currentCode = "";

eyepad.init = function(padId) {
	eyepad.padId = padId;
  eyepad.editor = ace.edit("code");
  eyepad.editor.getSession().setMode("ace/mode/clojure");
  eyepad.editor.getSession().on("change", eyepad.throttle(function(e) {
    eyepad.evaluate(eyepad.editor.getValue());
  }, 250));
	eyepad.evaluate(eyepad.editor.getValue());
};
  
// http://remysharp.com/2010/07/21/throttling-function-calls/
eyepad.throttle = function(fn, delay) {
  var timer = null;
  return function () {
    var context = this, args = arguments;
    clearTimeout(timer);
    timer = setTimeout(function () {
      fn.apply(context, args);
    }, delay);
  };
};

eyepad.evaluate = function(code) {
  if (code == eyepad.currentCode) return;
  eyepad.currentCode = code;
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