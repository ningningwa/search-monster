const domain =
  !process.env.NODE_ENV || process.env.NODE_ENV === 'development'
    ? 'http://ec2-52-207-237-70.compute-1.amazonaws.com:8001'
    : '';


const querySearch = async (query) => {

    const url = domain + '/search/' + query;

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
export { productSearch };