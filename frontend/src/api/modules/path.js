import ApiService from '@/api'

const BASE_URL = '/paths'

const PathService = {
  get(searchingPath) {
    return ApiService.get(`${BASE_URL}?source=${searchingPath.source}&target=${searchingPath.target}&type=${searchingPath.type}`);
  }
}

export default PathService
