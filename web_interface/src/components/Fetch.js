// const domain = 'http://localhost:8001';
const domain = 'http://ec2-34-203-249-121.compute-1.amazonaws.com:8001';

const querySearch = async (query) => {

    const url = domain + '/search/' + query;

    const results = await fetch(url, {
        method: 'GET'
    });

    const json = await results.json();
    return json;
}

const querySearchLarge = async (query) => {

    const url = domain + '/searchL/' + query;

    const results = await fetch(url, {
        method: 'GET'
    });

    const json = await results.json();
    return json;
}

const productSearch = async (query) => {
    const url = domain + '/product/' + query;

    const results = await fetch(url, {
        method: 'GET'
    });

    return await results.json();
}

export default querySearch;
export { productSearch, querySearchLarge };