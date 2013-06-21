function confirmDelete(t){
    var confirmed = confirm('Les données seront définitivement supprimées !')
    if(confirmed){
        t.form.submit();
    }
}