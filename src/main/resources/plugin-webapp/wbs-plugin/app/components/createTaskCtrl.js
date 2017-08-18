ngDefine('cockpit.plugin.wbs-plugin.componentsModule', function(module) {
  
	module.controller('createTaskCtrl', ['$scope','$modalInstance', 'tasks', function($scope, $modalInstance, tasks) {
	  
	  
	  var init = function(){
		  $scope.tiposTarefa = tasks;
		  console.log(tasks)
	  };
		
	  init();
    
	  $scope.save = function() {
		  $modalInstance.close();
	  }
    
	  $scope.cancel = function() {
		  $modalInstance.dismiss('cancel');
	  }
    
    
	}])
});