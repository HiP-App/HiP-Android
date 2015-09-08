var World = {
	loaded: false,

	init: function initFn() {
		this.createOverlays();
	},

	createOverlays: function createOverlaysFn() {
		/*
			First an AR.ClientTracker needs to be created in order to start the recognition engine. It is initialized with a URL specific to the target collection. Optional parameters are passed as object in the last argument. In this case a callback function for the onLoaded trigger is set. Once the tracker is fully loaded the function worldLoaded() is called.

			Important: If you replace the tracker file with your own, make sure to change the target name accordingly.
			Use a specific target name to respond only to a certain target or use a wildcard to respond to any or a certain group of targets.
		*/
		this.tracker = new AR.ClientTracker("assets/Liboribrunnen.wtc", {
			onLoaded: this.worldLoaded
		});

		var headLabel = new AR.Label("  +  ",0.2,
			{
				offsetX : -0.2,
				offsetY : 0.4,
				style : {textColor : '#C10000',backgroundColor : '#FFFFFF80'},
				verticalAnchor : AR.CONST.VERTICAL_ANCHOR.TOP,
				horizontalAnchor : AR.CONST.HORIZONTAL_ANCHOR.LEFT,
				onClick : function(){
					if(headLabel.visibility){
						headLabel.height = 0.2;
						headLabel.text = "This is a staff";
						headLabel.visibility = false;
					}
					else {
						headLabel.height = 0.2;
						headLabel.text = "  +  ";
						headLabel.visibility = true;
					}
				}
			}
		);
		headLabel.visibility = false;

		var legsLabel = new AR.Label("  +  ",0.2,
			{
				offsetX : 0.0,
				offsetY : -0.2,
				style : {textColor : '#C10000',backgroundColor : '#FFFFFF80'},
				verticalAnchor : AR.CONST.VERTICAL_ANCHOR.TOP,
				horizontalAnchor : AR.CONST.HORIZONTAL_ANCHOR.LEFT,
				onClick : function(){
                					if(legsLabel.visibility){
                						legsLabel.height = 0.2;
                						legsLabel.text = "This is a well";
                						legsLabel.visibility = false;
                					}
                					else {
                						legsLabel.height = 0.2;
                						legsLabel.text = "  +  ";
                						legsLabel.visibility = true;
                					}
                				}
			}
		);
		legsLabel.visibility = false;

		/*
			The last line combines everything by creating an AR.Trackable2DObject with the previously created tracker, the name of the image target and the drawable that should augment the recognized image.
			Please note that in this case the target name is a wildcard. Wildcards can be used to respond to any target defined in the target collection. If you want to respond to a certain target only for a particular AR.Trackable2DObject simply provide the target name as specified in the target collection.
		*/
		var pageOne = new AR.Trackable2DObject(this.tracker, "*", {
			drawables: {
				cam: [headLabel, legsLabel]
			}
		});
	},

	worldLoaded: function worldLoadedFn() {
		var cssDivLeft = " style='display: table-cell;vertical-align: middle; text-align: right; width: 50%; padding-right: 15px;'";
		var cssDivRight = " style='display: table-cell;vertical-align: middle; text-align: left;'";
		document.getElementById('loadingMessage').innerHTML =
			"<div" + cssDivLeft + ">Scan Target &#35;1 (Liboribrunnen):</div>" +
			"<div" + cssDivRight + "><img src='assets/Liboribrunnen.jpg'></img></div>";

		// Remove Scan target message after 10 sec.
		setTimeout(function() {
			var e = document.getElementById('loadingMessage');
			e.parentElement.removeChild(e);
		}, 10000);
	}
};

World.init();
