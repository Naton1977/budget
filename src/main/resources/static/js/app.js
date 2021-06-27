window.addEventListener("load", () => {

    let users = [];

    let createTableUsers = function (e) {
        if (e.target.id === "button1") {
            document.getElementById('pushContainer').innerHTML;
            let tmp = '';
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
            tmp += '<tbody id="userTableTbody"></tbody>';
            document.getElementById('pushContainer').innerHTML = tmp;
            getUser();
        }
    }

    let getUser = function () {
        fetch('http://localhost:8080/api/v1/product/findAllUsers')
            .then(response => response.json())
            .then(responce => creatTbody(responce));
    }


    let creatTbody = function (responce) {
        users = responce
        console.log(users);
        let tmp = '';
        for (let i = 0; i < users.length; i++) {
            tmp += '<tr>';
            tmp += '<td>' + (i + 1) + '</td>';
            tmp += '<td>' + users[i].firstName + '</td>';
            tmp += '<td>' + users[i].lastName + '</td>';
            tmp += '<td>' + users[i].patronymic + '</td>';
            tmp += '<td>' + users[i].memberLogin + '</td>';
            tmp += '</tr>';
            document.getElementById('userTableTbody').innerHTML = tmp;
        }
    }


    let showFamilyAccount = function (e) {
        if (e.target.id === 'button2') {
            let tmp = '';
            let familyAccount;
            fetch('http://localhost:8080/api/v1/product/findFamilyAccount')
                .then(response => response.json())
                .then(responce => {
                    tmp = '';
                    tmp += '<p>Состояние счета Вашей семьи : ' + responce + ' грн' + '</p>'
                    document.getElementById("pushContainer").innerHTML = tmp;
                })
        }
    }


    let pushManyOnAccount = function (e) {
        if (e.target.id === "button3") {
            document.getElementById("pushContainer").innerHTML;
            let tmp = '';
            tmp += '<p>Введите сумму которую Вы хотите положить на счет' + '    ' + '<input id="inputPushMany" type="text">' + '    ' + '<button id="buttonPushMany">Положить</button>' + '</p>';
            document.getElementById("pushContainer").innerHTML = tmp;
        }
    }

    let sendMany = async function (e) {
        if (e.target.id === 'buttonPushMany') {
            let inputValue = document.getElementById('inputPushMany').value;
            var formdata = new FormData();
            formdata.append("manyCount", inputValue);

            var requestOptions = {
                method: 'PUT',
                body: formdata,
                redirect: 'follow'
            };

            fetch("http://localhost:8080/api/v1/product/putMany", requestOptions)
                .then(response => response.json())
                .then(result => showAnswer(result))
                .catch(error => console.log('error', error));

        }
    }


    let withdrawMoney = function (e) {
        if (e.target.id === 'button4') {
            document.getElementById("pushContainer").innerHTML;
            let tmp = '';
            tmp += '<p>Введите сумму которую Вы хотите снять со счета' + '    ' + '<input id="withdrawMany" type="text">' + '    ' + '<button id="buttonWithdrawMany">Снять</button>' + '</p>';
            document.getElementById("pushContainer").innerHTML = tmp;
        }
    }

    let takeMoney = async function (e) {
        if (e.target.id === 'buttonWithdrawMany') {
            let inputValue = document.getElementById('withdrawMany').value;
            var myHeaders = new Headers();
            myHeaders.append("X-CSRFT-TOKEN", "");

            var formdata = new FormData();
            formdata.append("manyCount", inputValue);

            var requestOptions = {
                method: 'PUT',
                headers: myHeaders,
                body: formdata,
                redirect: 'follow'
            };

            fetch("http://localhost:8080/api/v1/product/withdrawMoney", requestOptions)
                .then(response => response.json())
                .then(result => showAnswer(result))
                .catch(error => console.log('error', error));

        }
    }
    let imposeRestrictions = function (e) {
        if (e.target.id === 'button5') {
            fetch('http://localhost:8080/api/v1/product/findAllUsers')
                .then(response => response.json())
                .then(responce => createOption(responce));
        }
    }

    let createOption = function (responce) {
        let users = responce;
        let tmp = '';
        document.getElementById("pushContainer").innerHTML;
        tmp += '<p>Выберите логин который нужно заблокировать</p>';
        tmp += '<select id="selectUser">';
        tmp += '<option>На всех</option>';
        for (let i = 0; i < users.length; i++) {
            tmp += '<option>' + users[i].memberLogin + '</option>';
        }
        tmp += '</select>';
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

    let sendAdminLimitations = function (e) {
        if (e.target.id === 'sendLimitations') {
            let select = document.getElementById("selectUser");
            let userLogin = select.options[select.selectedIndex].value;
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

            fetch("http://localhost:8080/api/v1/product/applyLimitations", requestOptions)
                .then(response => response.json())
                .then(result => showAnswer(result))
                .catch(error => console.log('error', error));


        }
    }

    let showAnswer = function (result) {
        let message = result;
        document.getElementById('pushContainer').innerHTML;
        let tmp = '';
        tmp += '<p>' + message.message + '</p>';
        let family = message.family;
        if (family !== null && typeof family !== 'undefined') {
            tmp += '<p>Состояние счета семьи после операции : ' + family.familyAccount + '  грн' + '</p>';
        }
        document.getElementById('pushContainer').innerHTML = tmp;
    }


    document.body.addEventListener('click', createTableUsers);
    document.body.addEventListener('click', showFamilyAccount);
    document.body.addEventListener('click', pushManyOnAccount);
    document.body.addEventListener('click', sendMany);
    document.body.addEventListener('click', withdrawMoney);
    document.body.addEventListener('click', takeMoney);
    document.body.addEventListener('click', imposeRestrictions);
    document.body.addEventListener('click', sendAdminLimitations);


})