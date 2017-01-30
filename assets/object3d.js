
(function() {
  var getStr = function(x) {
    if (typeof x === 'string') {
      return x;
    } else {
      return x.toString();
    }
  };

  THREE.Object3D.prototype.addBy = function(k, v) {
    if (this.childMap == null) {
      this.childMap = {};
    }
    this.childMap[getStr(k)] = v;
    this.add(v);
  };

  THREE.Object3D.prototype.removeBy = function(k) {
    if (this.childMap == null) {
      console.warn('Calling removeBy without childMap');
    } else {
      var v = this.childMap[getStr(k)];
      delete this.childMap[getStr(k)];
      this.remove(v);
    }
  };
})();
