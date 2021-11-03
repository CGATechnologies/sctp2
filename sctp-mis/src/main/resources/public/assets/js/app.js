(function () {
    window.query = function(exp){ return document.querySelector(exp); }
    window.queryAll = function(exp){ return document.querySelectorAll(exp); }
    var burger = query('.overflow-menu');
    if(burger){
        var menu = query('#' + burger.dataset.target);
        burger.addEventListener('click', function () {
            burger.classList.toggle('is-active');
            menu.classList.toggle('is-active');
        });
    }
    window.meta = function(name){
        var m = query(`meta[name='${name}']`);
        if(m != null){
            return m.getAttribute('content');
        }
        return null;
    }
    window.replaceClass = function(el, a, b){
      if(el.dataset.class == a){
        el.dataset.class = b;
      }else{
        el.dataset.class = a;
      }
      el.classList.remove(a, b);
      el.classList.add(el.dataset.class);
    }
    window.setupDataTables = function(...elements){
        var _results = [];
        var options = {perPage: 50, perPageSelect: [5, 10, 20, 25, 50, 100]};
        for(i=0; i< elements.length;i++){
            _results.push(new simpleDatatables.DataTable(elements[i], options));
        }
        return _results;
    };
    _dataTable_ = query('.dataTable');
    if(_dataTable_){
        window.firstDataTable = new simpleDatatables.DataTable(
            _dataTable_,
            {
                perPage: 50,
                perPageSelect: [5, 10, 20, 25, 50, 100]
            }
        )
    }
    HTMLElement.prototype.fireEvent = function(name,opts=null){ this.dispatchEvent(new Event(name, opts || {})); };
    window.templateFromHtml = function(html, format = 'first_child'){
        const formats = ['template', 'first_child', 'child_nodes'];
        var tpl = document.createElement('template');
        tpl.innerHTML = html;
        if(format === 'template'){
            return tpl;
        }
        if(format === 'first_child'){
            return tpl.content.firstChild;
        }
        if(format === 'child_nodes'){
            return tpl.content.childNodes;
        }
        throw 'format parameter must be one of: ' + formats.join(',');
    };
    window.downloadTableToCsv = function(name, skipColumns){
        var opts = {type:'csv', download: true, filename: name};
        if(skipColumns){
            opts['skipColumn'] = skipColumns;
        }
        firstDataTable.export(opts);
    }
    window.createResumableFile = function(file, options){
        if("tus" in window){
            if(!("retryDelays" in options)){
                options["retryDelays"] = [0, 3000, 5000];
            }
            return new tus.Upload(file, options);
        }else{
            console.error('tus library not loaded.');
            return null;
        }
    };
    window.uploadResumableFile = function(tusUploadHandle, resumeHint = true){
        if(resumeHint){
            tusUploadHandle.findPreviousUploads().then(function (previousUploads) {
                if (previousUploads.length) {
                    tusUploadHandle.resumeFromPreviousUpload(previousUploads[0])
                }
                tusUploadHandle.start()
            });
        }else{
            tusUploadHandle.start();
        }
    };
})();