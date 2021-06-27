window.addEventListener("load", () => {


    let showAllFamilyList = function (e) {
        if (e.target.id === "button1") {
            let tmp = '';
            document.getElementById("pushContainer").innerHTML;
            tmp += '<table>';
            tmp += '<thead>';
            tmp += '<tr>';
            tmp += '<td>н/п.п</td>';
            tmp += '<td>логин семьи</td>';
            tmp += '<td>имя админа семьи</td>';
            tmp += '<td>отчество админа семьи</td>';
            tmp += '<td>фамилия админа семьи</td>';
            tmp += '<td>счет семьи</td>';
            tmp += '</tr>';
            tmp += '</thead>';
            tmp += '<tbody id="globalAdminTbody"></tbody>';
            tmp += '</table>';
            document.getElementById("pushContainer").innerHTML = tmp;
            getAllFamily();
        }
    }

    let getAllFamily = function () {
        var requestOptions = {
            method: 'GET',
            redirect: 'follow'
        };

        fetch("http://localhost:8080/api/v1/product/allFamilyList", requestOptions)
            .then(response => response.json())
            .then(result => createAllFamilyTbody(result))
            .catch(error => console.log('error', error));
    }

    let createAllFamilyTbody = function (result) {
        let tmp = '';
        let allFamily = result;
        for (let i = 0; i < allFamily.length; i++) {
            tmp += '<tr>';
            tmp += '<td>' + (i + 1) + '</td>';
            tmp += '<td>' + allFamily[i].familyLogin + '</td>';
            tmp += '<td>' + allFamily[i].firstName + '</td>';
            tmp += '<td>' + allFamily[i].patronymic + '</td>';
            tmp += '<td>' + allFamily[i].lastName + '</td>';
            tmp += '<td>' + allFamily[i].familyAccount + '</td>';
            tmp += '</tr>';
        }
        document.getElementById('globalAdminTbody').innerHTML = tmp;
    }

    let allFamilyMember = function (e) {
        if (e.target.id === 'button2') {
            let tmp = '';
            tmp += '<p>Введите логин семьи' + '   ' + '<input id="familyLogin" type="text">' + '  ' + '<button id="allUserFamilyButton">Отправить</button>' + '</p>';
            document.getElementById('pushContainer').innerHTML;
            document.getElementById('pushContainer').innerHTML = tmp;


        }
    }

    let getAllUserFamily = function (e) {
        if (e.target.id === 'allUserFamilyButton') {
            let familyLogin = document.getElementById("familyLogin").value;

            var formdata = new FormData();
            formdata.append("familyLogin", familyLogin);

            var requestOptions = {
                method: 'POST',
                body: formdata,
                redirect: 'follow'
            };

            fetch("http://localhost:8080/api/v1/product/findByFamilyLogin", requestOptions)
                .then(response => response.json())
                .then(result => createTableAllUserFamily(result))
                .catch(error => console.log('error', error));


        }
    }

    let createTableAllUserFamily = function (result) {
        let allUsers = result;
        tmp = '';
        document.getElementById('pushContainer').innerHTML;
        tmp += '<table>';
        tmp += '<thead>';
        tmp += '<tr>';
        tmp += '<td>Н.пп</td>';
        tmp += '<td>Фамилия</td>';
        tmp += '<td>Имя</td>';
        tmp += '<td>Отчество</td>';
        tmp += '<td>Логин</td>';
        tmp += '</tr>';
        tmp += '</thead>';
        tmp += '<tbody>';
        for (let i = 0; i < allUsers.length; i++) {
            tmp += '<tr>';
            tmp += '<td>' + (i + 1) + '</td>';
            tmp += '<td>' + allUsers[i].lastName + '</td>';
            tmp += '<td>' + allUsers[i].firstName + '</td>';
            tmp += '<td>' + allUsers[i].patronymic + '</td>';
            tmp += '<td>' + allUsers[i].memberLogin + '</td>';
            tmp += '</tr>';
        }
        tmp += '</tbody>';
        tmp += '</table>';
        document.getElementById('pushContainer').innerHTML = tmp;

    }

    let showFamilyAccount = function (e) {
        if (e.target.id === "button3") {
            document.getElementById("pushContainer").innerHTML;
            let tmp = '';
            tmp += '<p>Введите логин семьи' + '   ' + '<input id="familyLoginAccount" type="text">' + '  ' + '<button id="showFamilyAccountButton">Отправить</button>' + '</p>';
            document.getElementById('pushContainer').innerHTML = tmp;


        }
    }

    let getFamilyAccount = function (e) {
        if (e.target.id === 'showFamilyAccountButton') {
            let familyLogin = document.getElementById("familyLoginAccount").value;

            var formdata = new FormData();
            formdata.append("familyLogin", familyLogin);

            var requestOptions = {
                method: 'POST',
                body: formdata,
                redirect: 'follow'
            };

            fetch("http://localhost:8080/api/v1/product/familyAccount", requestOptions)
                .then(response => response.json())
                .then(result => showAccount(result))
                .catch(error => console.log('error', error));
        }


        let showAccount = function (result) {
            let acc = result;
            document.getElementById("pushContainer").innerHTML;
            let tmp = '';
            tmp += '<p>Счет запрошенной Вами семьи  :  ' + acc.manyCount + '</p>';
            document.getElementById("pushContainer").innerHTML = tmp;
        }
    }

    let putManyForFamilyAccount = function (e) {
        if (e.target.id === 'button4') {
            document.getElementById("pushContainer").innerHTML;
            let tmp = '';
            tmp += '<p>Введите логин семьи на счет которой нужно положить деньги</p>';
            tmp += '<input id="loginPutMany" type="text">' + '<br>';
            tmp += '<p>Введите сумму пополнения</p>';
            tmp += '<input id="putMany" type="number">';
            tmp += '<p></p>';
            tmp += '<button id="sendMany">Отправить</button>';
            document.getElementById("pushContainer").innerHTML = tmp;
        }
    }

    let sendManyForFamilyAccount = function (e) {
        if (e.target.id === 'sendMany') {
            let familyLogin = document.getElementById('loginPutMany').value;
            let many = document.getElementById("putMany").value;
            var formdata = new FormData();
            formdata.append("familyLogin", familyLogin);
            formdata.append("many", many);

            var requestOptions = {
                method: 'PUT',
                body: formdata,
                redirect: 'follow'
            };

            fetch("http://localhost:8080/api/v1/product/putManyGlobalAdmin", requestOptions)
                .then(response => response.json())
                .then(result => showAnswer(result))
                .catch(error => console.log('error', error));
        }
    }

    let showAnswer = function (result) {
        let message = result;
        let family = message.family;
        document.getElementById('pushContainer').innerHTML;
        let tmp = '';
        tmp += '<p>' + message.message + '</p>';
        if (family !== null) {
            tmp += '<p>Состояние счета семьи после операции : ' + family.familyAccount + '  грн' + '</p>';
        }
        document.getElementById('pushContainer').innerHTML = tmp;
    }


    let withdrawMoneyFromFamilyAccount = function (e) {
        if (e.target.id === 'button5') {
            document.getElementById('pushContainer').innerHTML;
            let tmp = '';
            tmp += '<p>Введите логин семьи со счета которой нужно снять деньги</p>';
            tmp += '<input id="loginWithdrawMany" type="text">' + '<br>';
            tmp += '<p>Введите сумму снятия</p>';
            tmp += '<input id="withdrawMany" type="number">';
            tmp += '<p></p>';
            tmp += '<button id="withdrawManyButton">Отправить</button>';
            document.getElementById("pushContainer").innerHTML = tmp;
        }
    }
    let sendWithdrawMany = function (e) {
        if (e.target.id === "withdrawManyButton") {
            let familyLogin = document.getElementById("loginWithdrawMany").value;
            let many = document.getElementById("withdrawMany").value;
            var formdata = new FormData();
            formdata.append("familyLogin", familyLogin);
            formdata.append("many", many);

            var requestOptions = {
                method: 'PUT',
                body: formdata,
                redirect: 'follow'
            };

            fetch("http://localhost:8080/api/v1/product/withdrawManyGlobalAdmin", requestOptions)
                .then(response => response.json())
                .then(result => showAnswer(result))
                .catch(error => console.log('error', error));
        }
    }


    let imposeRestrictionsOnAllFamily = function (e) {
        if (e.target.id === 'button6') {
            document.getElementById('pushContainer').innerHTML;
            let tmp = '';
            tmp += '<p>Введите логин семьи на которую необходимо наложить ограничения (All - ограничения будут наложены на все семьи)</p>';
            tmp += '<input id="familyLogin" type="text"> ';
            tmp += '<p>Введите сумму максимального единоразового снятия</p>';
            tmp += '<input id="maxOnes" type="number">';
            tmp += '<p>Введите суму максимального снятия за день</p>';
            tmp += '<input id="maxPerDay" type="number">';
            tmp += '<p>Введите дату начала ограничения</p>';
            tmp += '<input id="dateStart" type="date">';
            tmp += '<p>Введите дату конца ограничения</p>';
            tmp += '<input id="dateEnd" type="date">';
            tmp += '<p></p>';
            tmp += '<button id="sendGlobalAdminLimitations">Отправить</button>';
            document.getElementById("pushContainer").innerHTML = tmp;
        }
    }


    let sendGlobalAdminLimitations = function (e) {
        if (e.target.id === 'sendGlobalAdminLimitations') {
            let familyLogin = document.getElementById("familyLogin").value;
            let maxOnes = document.getElementById('maxOnes').value;
            let maxPerDay = document.getElementById('maxPerDay').value;
            let dateStart = document.getElementById("dateStart").value;
            let dateEnd = document.getElementById("dateEnd").value;
            var formdata = new FormData();
            formdata.append("familyLogin", familyLogin);
            formdata.append("maximumOneTimeWithdrawalPerDay", maxOnes);
            formdata.append("maximumWithdrawalPerDay", maxPerDay);
            formdata.append("dateStartLimitation", dateStart);
            formdata.append("dateEndLimitation", dateEnd);

            var requestOptions = {
                method: 'POST',
                body: formdata,
                redirect: 'follow'
            };

            fetch("http://localhost:8080/api/v1/product/globAdmLimAllFamily", requestOptions)
                .then(response => response.json())
                .then(result => showAnswer(result))
                .catch(error => console.log('error', error));


        }
    }

    let createOption = function (e) {
        if (e.target.id === "button7") {
            let tmp = '';
            document.getElementById("pushContainer").innerHTML;
            tmp += '<p>Выберите логин который нужно заблокировать</p>';
            tmp += '<input id="logUs" type="text">';
            tmp += '<p>Введите сумму максимального единоразового снятия</p>';
            tmp += '<input id="maxOnes" type="number">';
            tmp += '<p>Введите сумму максимального снятия за день</p>';
            tmp += '<input id="maxPerDay" type="number">';
            tmp += '<p>Введите дату начала ограничения</p>';
            tmp += '<input id="dateStart" type="date">';
            tmp += '<p>Введите дату конца ограничения</p>';
            tmp += '<input id="dateEnd" type="date">';
            tmp += '<p></p>';
            tmp += '<button id="sendLimitations">Отправить</button>';
            document.getElementById("pushContainer").innerHTML = tmp;
        }
    }

    let sendGlobalAdminPersonalLimitations = function (e) {
        if (e.target.id === 'sendLimitations') {
            let userLogin = document.getElementById("logUs").value;
            let maxOnes = document.getElementById('maxOnes').value;
            let maxPerDay = document.getElementById('maxPerDay').value;
            let dateStart = document.getElementById("dateStart").value;
            let dateEnd = document.getElementById("dateEnd").value;

            var formdata = new FormData();
            formdata.append("familyMemberLogin", userLogin);
            formdata.append("maximumOneTimeWithdrawalPerDay", maxOnes);
            formdata.append("maximumWithdrawalPerDay", maxPerDay);
            formdata.append("dateStartLimitation", dateStart);
            formdata.append("dateEndLimitation", dateEnd);

            var requestOptions = {
                method: 'POST',
                body: formdata,
                redirect: 'follow'
            };

            fetch("http://localhost:8080/api/v1/product/globAdmPersLim", requestOptions)
                .then(response => response.json())
                .then(result => showAnswer(result))
                .catch(error => console.log('error', error));


        }
    }


    document.body.addEventListener('click', showAllFamilyList);
    document.body.addEventListener('click', allFamilyMember);
    document.body.addEventListener('click', getAllUserFamily);
    document.body.addEventListener('click', showFamilyAccount);
    document.body.addEventListener('click', getFamilyAccount);
    document.body.addEventListener('click', putManyForFamilyAccount);
    document.body.addEventListener('click', sendManyForFamilyAccount);
    document.body.addEventListener('click', withdrawMoneyFromFamilyAccount);
    document.body.addEventListener('click', sendWithdrawMany);
    document.body.addEventListener('click', imposeRestrictionsOnAllFamily);
    document.body.addEventListener('click', sendGlobalAdminLimitations);
    document.body.addEventListener('click', createOption);
    document.body.addEventListener('click', sendGlobalAdminPersonalLimitations);


})
