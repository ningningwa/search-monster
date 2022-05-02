const domain =
  !process.env.NODE_ENV || process.env.NODE_ENV === 'development'
    ? 'http://localhost:8001'
    : '';


const querySearch = async (query) => {

    const url = domain + '/search/' + query;

    const results = await fetch(url, {
        method: 'GET'
    });

    const json = await results.json();
    return json;
}

export default querySearch;