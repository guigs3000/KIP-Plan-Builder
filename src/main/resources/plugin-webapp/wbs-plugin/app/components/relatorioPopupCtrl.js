ngDefine('cockpit.plugin.wbs-plugin.componentsModule', function(module) {
  
	module.controller('relatorioPopupCtrl', ['$scope','$modalInstance', 'inconformidades', function($scope, $modalInstance, inconformidades) {
	  
	  
	  var init = function(){
		  $scope.inconformidades = inconformidades;
	  };
		
	  init();
    
	  $scope.save = function() {
		  
	  };
    
	  $scope.cancel = function() {
		  $modalInstance.dismiss('cancel');
	  };
    
    
	}])
});