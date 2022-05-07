import React from 'react';
import ProductItem from './ProductItem';

const Products = ({ products }) => {
    return (
        <div className="ui segment">
            <h4 class="ui center aligned icon header">
                <i class="circular shopping cart icon"></i>
               You may like these from Amazon...
            </h4>

            <div class="ui divider"></div>

            <div className="ui divided items">
                {
                    products.map(p => <div className="item">{<ProductItem product={p} />}</div>)
                }
            </div>
        </div>
    );
}

export default Products;